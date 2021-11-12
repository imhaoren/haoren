package com.haoren.sharding.strategy;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Collection;

public class PreciseShardingAlgorithmStrategy implements PreciseShardingAlgorithm<Long> {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
        for (String availableTargetName : availableTargetNames) {
            //判断是否是数据源名
            if (availableTargetName.startsWith("ds") && availableTargetName.endsWith(String.valueOf(shardingValue.getValue() % 2))) {
                return availableTargetName;
            }

            if ("user".equals(availableTargetName)) {
                return availableTargetName + "_" + shardingValue.getValue() % 4;
            }
        }
        throw new UnsupportedOperationException();
    }
}
