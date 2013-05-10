local center = layout.getCenterField()
local tracks = { center.getNeighbour('ru', 6).getEdge('l').addTrack('straight') }

local addTrack = function(type)
	table.insert(tracks, tracks[#tracks].getEndEdge().addTrack(type))
end

local addTurnaround = function()
	addTrack('straight')
	addTrack('curve-right')
	addTrack('curve-right')
	addTrack('curve-right')
	addTrack('curve-right')
	addTrack('curve-left')
end

local haltAspect = { distance = 0, speed = 0 }
local fahrtAspect = { distance = 0, speed = .4 }

addTrack('straight')
addTrack('straight')
addTrack('straight')
addTurnaround()
addTrack('straight')
addTrack('straight')
addTrack('straight')
addTrack('straight')
addTurnaround()


local function previousTrack(track)
	return track.getStartEdge().reverse().getActiveTrack().reverse()
end

local function nextTrack(track)
	return track.getEndEdge().getActiveTrack()
end

local function maxSpeedForDistance(distance)
	if distance >= 4 then
		return 1.6
	--elseif distance >= 2 then
	--	return 0.6
	elseif distance >= 1 then
		return 0.4
	else
		return 0.0
	end
end

local function manageTrain(track, safeDistanceFn)
	track.getField().awaitState('free', function ()
		local affectingSignal = track.getAffectingSignals()[1]
		local nextTrack = nextTrack(track)
		local done = false

		simulation.schedule(.5, function ()
			manageTrain(nextTrack, function (safeDistance)
				if not done then
					safeDistanceFn(safeDistance + 1)

					if affectingSignal then
						print('distance', safeDistance)
						affectingSignal.setAspect({ distance = 0, speed = maxSpeedForDistance(safeDistance) })
					end
				end
			end)
		end)
		
		if affectingSignal then
			print('halt')
			affectingSignal.setAspect(haltAspect)
			safeDistanceFn(1)
		end

		track.getField().awaitState('occupied', function ()
			track.getField().awaitState('free', function ()
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

	--if i % 4 == 0 then
	--	tracks[i].reverse().addSignal('dwarf')
	--end
	
--	if i % 6 == 0 then
end

for _, i in ipairs({ 1, 7 }) do
	local track = tracks[i]
	local train = track.addTrain(.8);

	train.addCar(.8)
	train.setTargetSpeed(.4);

	manageTrain(nextTrack(track), function () end)
end

do
	local tracks = { }

	for i = 1, 40 do
		tracks[i] = center.getNeighbour('ru', 2).getNeighbour('r', i - 2).getEdge('l').addTrack('straight')
	end

	tracks[2].addTrain(.8).setTargetSpeed(1.0)
	tracks[12].addSignal('dwarf').setAspect({ distance = 0, speed = .4 })
	tracks[16].addSignal('dwarf').setAspect({ distance = 0, speed = 0 })
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

