state.var {
  TXIds = state.map(),
  TXCount = state.value()
}

function constructor()
  TXCount:set(1)
end

function setTXId()
  TXIds[TXCount:get()] = system.getTxhash()
  TXCount:set(TXCount:get() + 1)
end

function getTXId(idx)
  return TXIds[idx]
end

abi.register_view(getTXId)
abi.register(setTXId)
