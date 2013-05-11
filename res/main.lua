local center = layout.getCenterField()
local tracks = { }

local haltAspect = { distance = 0, speed = 0 }
local fahrtAspect = { distance = 0, speed = .4 }

do
	local currentEdge = center.getNeighbour('ru', 2).getNeighbour('r', 2).getEdge('l')
	
	local addTrack = function(type)
		local newTrack = currentEdge.addTrack(type)
		
		currentEdge = newTrack.getEndEdge()
		
		table.insert(tracks, newTrack)
	end
	
	local addTurnaround = function()
		addTrack('straight')
		addTrack('curve-right')
		addTrack('curve-right')
		addTrack('curve-right')
		addTrack('curve-right')
		addTrack('curve-left')
	end
	
	for i = 1, 9 do
		addTrack('straight')
	end
	
	addTurnaround()
	
	for i = 1, 9 do
		addTrack('straight')
	end
	
	addTurnaround()
end

--divert('straight', divert('curve-left', 'curve-right', 'curve-right', 'curve-left'), 'straight', 'straight', 'straight')
--
--'s[lrrl]sss'


local function previousTrack(track)
	return track.getStartEdge().reverse().getActiveTrack().reverse()
end

local function nextTrack(track)
	return track.getEndEdge().getActiveTrack()
end

local function maxSpeedForDistance(distance)
	local data = {{0.8,0.4},{1.8,0.6},{5.,1.},{7.2,1.2},{9.8,1.4},{12.8,1.6}}
	local foundMaxSpeed = 0.0
	
	for _, i in ipairs(data) do
		local d, s = table.unpack(i)
		
		if d < distance - 0.1 and s > foundMaxSpeed then
			foundMaxSpeed = s
		end
	end
	
	return foundMaxSpeed
end 

local function manageTrain(track, safeDistanceFn)
	track.getField().awaitState('free', function ()
		local affectingSignal = track.getAffectingSignals()[1]
		local nextTrack = nextTrack(track)
		local done = false

		manageTrain(nextTrack, function (safeDistance, nextMaxSpeed)
			if not done then
				safeDistance = safeDistance + 1

				if affectingSignal then
					affectingSignal.setAspect({ distance = 0, speed = nextMaxSpeed })

					nextMaxSpeed = maxSpeedForDistance(safeDistance)
				end

				safeDistanceFn(safeDistance, nextMaxSpeed)
			end
		end)

		if affectingSignal then
			affectingSignal.setAspect(haltAspect)
			safeDistanceFn(1, .4)
		end
		
		simulation.setColor(track, { 1, 1, 0 })

		track.getField().awaitState('occupied', function ()
			track.getField().awaitState('free', function ()
				simulation.setColor(track, { 1, 1, 1 })
				done = true 
			end)
		end) 
	end)
end 


--for _, i in ipairs({ 20, 2, 4, 6, 10, 12, 14, 16 }) do
--	setupSignal(tracks[i].addSignal('dwarf'))
--end

for i = 1, #tracks do
	if i % 3 == 0 then
		tracks[i].addSignal('system-l')
	end
end

do
	for _, i in ipairs({ 7, 10, 13, 26 }) do 
		local track = tracks[i]
		local train = track.addTrain(.8);
	
		train.addCar(.8)
		train.setTargetSpeed(.4);
	
		manageTrain(nextTrack(track), function () end)
	end
	
	local train = tracks[1].addTrain(.8)
	train.setTargetSpeed(.4); 
	
	simulation.setKeyHandler('k', function ()
		train.setTargetSpeed(0)
	end)
end

do
	local tracks = { }

	for i = 1, 40 do
		tracks[i] = center.getNeighbour('ru', 4).getNeighbour('r', i - 3).getEdge('l').addTrack('straight')
	end

	tracks[2].addTrain(.8).setTargetSpeed(.4)
	--tracks[12].addSignal('dwarf').setAspect({ distance = 0, speed = .4 })
	tracks[4].addSignal('dwarf').setAspect({ distance = 0, speed = 0 })
end

--(function ()
--	local tracks = { }
--
--	for i = 1, 10 do
--		tracks[i] = center.getNeighbour('ru', 0).getNeighbour('r', i - 1).getEdge('l').addTrack('straight')
--	end
--
--	--tracks[7].addSignal('dwarf').setAspect({ distance = 0, speed = 0 })
--	tracks[2].addTrain(.8).setTargetSpeed(2)
--end)()

