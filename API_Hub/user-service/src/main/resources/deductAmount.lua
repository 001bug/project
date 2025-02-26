-- 获取userId、interfaceId和price的值
local userId = tonumber(ARGV[1])
local price = tonumber(ARGV[2])

-- 查询ak:balance:{userId}
local balanceKey = "ak:balance:" .. userId
local balance = redis.call('get', balanceKey)

if balance and tonumber(balance) >= price then
    -- 如果余额大于等于price，进行相应的操作
    redis.call('decrby', balanceKey, price) -- 余额减去price
    return 0 -- 返回0表示操作成功
else -- 如果余额小于price
    return -1 -- 返回-1表示余额不足
end
