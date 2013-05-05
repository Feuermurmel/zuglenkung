
local center = layout.getCenterField()

for i = 0, 10 do
    center.getNeighbour('r', i).getEdge('r').addTrack('straight')
end

local randomStraightTrack = center.getNeighbour('r', 5).getEdge('l').addTrack('curve-left').getEndEdge().addTrack('straight')

local signal = randomStraightTrack.addSignal('dwarf')
signal.setAspect({ distance = 100, speed = 0 })

local randomCurveTrack = randomStraightTrack.getEndEdge().addTrack('curve-right').getEndEdge().addTrack('curve-right').getEndEdge().addTrack('curve-right')
local randomOtherSignal = randomCurveTrack.addSignal('dwarf')

simulation.setKeyHandler('b', function ()
    randomOtherSignal.setAspect({ distance = 100, speed = 1 })
end)

simulation.setKeyHandler('B', function ()
    randomOtherSignal.setAspect({ distance = 0, speed = 0 })
end)

randomCurveTrack.getEndEdge().addTrack('curve-right')

local train = center.getNeighbour('r', 3).getEdge('l').getTracks()[1].addTrain(.8);

train.addCar(.8)
train.setTargetSpeed(1);
