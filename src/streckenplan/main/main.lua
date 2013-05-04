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
local fahrtAspect = { distance = 100, speed = 0 }

addTrack('straight')
addTrack('straight')
addTrack('straight')
addTurnaround()
addTrack('straight')
addTrack('straight')
addTrack('straight')
addTrack('straight')
addTurnaround()


center.getNeighbour('ru', 1).getNeighbour('r', 2).getEdge('l').addTrack('curve-left').getEndEdge().addTrack('curve-right').getEndEdge().addTrack('curve-right').getEndEdge().addTrack('curve-left')

center.getNeighbour('ru', 1).getNeighbour('r', 13).getEdge('l').addTrack('curve-left').getEndEdge().addTrack('curve-right').getEndEdge().addTrack('curve-right').getEndEdge().addTrack('curve-left')

for i = 1, 20 do
	center.getNeighbour('ru', 2).getNeighbour('l', 2).getNeighbour('r', i).getEdge('l').addTrack('straight')
	center.getNeighbour('ru', 1).getNeighbour('l', 1).getNeighbour('r', i).getEdge('l').addTrack('straight')
end

local function setupSignal(signal)
	local tracks = signal.getAffectedTracks()
	local field = signal.getField()
	
	local function checkTrack(i)
		if i > #tracks then
			print('go')
			
			signal.setAspect(fahrtAspect)
			
			field.awaitState('occupied', function ()
				field.awaitState('free', function ()
					signal.setAspect(haltAspect)

					setupSignal(signal)
				end)
			end)
		else
			print('track')
			
			local function checkField(track)
				print('field')
				
				local nextTrack = track.getEndEdge().getActiveTrack()
				
				nextTrack.getField().awaitState('free', function ()
					if #nextTrack.getAffectingSignals() > 0 then
						checkTrack(i + 1)
					else
						checkField(nextTrack)
					end
				end)
			end
			
			checkField(tracks[i])
		end
	end

	checkTrack(1);
end

for i = 1, #tracks do
	if i % 4 == 1 then
		setupSignal(tracks[i].addSignal('dwarf'))
	end

	--if i % 4 == 0 then
	--	tracks[i].reverse().addSignal('dwarf')
	--end
	
	if i % 6 == 0 then
		local train = tracks[i].addTrain(.8);

		train.addCar(.8)
		train.setTargetSpeed(.4);	
	end
end



