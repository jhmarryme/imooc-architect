package com.jhmarryme.sharding;

import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.util.Collection;

/**
 *
 * @author JiaHao Wang
 * @date 2021/11/9 下午4:05
 */
public class OrderShardingAlgorithm implements StandardShardingAlgorithm<String> {
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        // 取出id
        String id = shardingValue.getValue();
        // 取模
        int mode = id.hashCode() % availableTargetNames.size();
        // 绝对值, 避免负数
        mode = Math.abs(mode);
        // 转换为数组, 并返回对应的targetName
        String[] strings = availableTargetNames.toArray(new String[0]);
        return strings[mode];
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<String> rangeShardingValue) {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public String getType() {
        return null;
    }
}