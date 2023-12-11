package com.example.familycloudstoragemanagement.UserManagement.mysecurity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

//雪花算法生成用户ID
public class IdGenerator {
    private final Long deviceTypeId; //设备类型
    //设备序列号,唯一标识同一时间戳与同一设备类型下的多个设备,初始值为0
    private Long sequence = 0L;

    /**
     * Instantiates a new Id generator.
     *
     * @param deviceType the device type
     */
    public IdGenerator(Long deviceType){
        //设备类型序号最大值
        Long maxDiviceTypeId = ~(-1L << diviceTypeBits);
        if (deviceType > maxDiviceTypeId || deviceType < 0) {
            throw new IllegalArgumentException(String.format("deviceType can't be greater than %d or less than 0", maxDiviceTypeId));
        }

        this.deviceTypeId = deviceType;


    }
    public IdGenerator(){
        this.deviceTypeId = 0L;

    }

    /**
     * Get device type id long.
     *
     * @return the long
     */
    public Long getDeviceTypeId(){
        return deviceTypeId;
    }

    /**
     * Get sequence long.
     *
     * @return the long
     */
    public Long getSequence(){
        return sequence;
    }

    //设备类型序号长度
    private final Long diviceTypeBits = 10L;

    //上次时间戳，初始值为负数
    private Long lastTimeStamp = -1L;

    /**
     * Gets time stamp.
     *
     * @return the time stamp
     */
//获取当前的时间戳
    public Long getTimeStamp() {
        ZoneId z = ZoneId.systemDefault();//获取当前时区
        //设置初始的时间戳
        LocalDateTime dt = LocalDateTime.parse("2022-01-01 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ZonedDateTime ztInit = dt.atZone(z);
        Long initTimeStamp = ztInit.toLocalDate().atStartOfDay(z).toEpochSecond();
        ZonedDateTime ztCurrent = ZonedDateTime.now(z);
        Long CurrentTimeStamp = ztCurrent.toEpochSecond();
        return CurrentTimeStamp - initTimeStamp;
    }

    /**
     * Get id long.
     *
     * @return the long
     */
    public synchronized Long getId(){
        Long timeStamp = getTimeStamp();

        //获取当前时间戳如果小于上次时间戳，则表示时间戳获取出现异常
        if (timeStamp < lastTimeStamp) {
            System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimeStamp);
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d seconds",
                    lastTimeStamp - timeStamp));
        }

        //获取当前时间戳如果等于上次时间戳（同一毫秒内），则在序列号加一；否则序列号赋值为0，从0开始。
        //序列号id长度
        Long sequenceBits = 22L;
        if (lastTimeStamp == timeStamp) {
            //序列号最大值
            Long sequenceMask = ~(-1L << sequenceBits);
            sequence = (sequence + 1) & sequenceMask;
            //如果序列号满了，就重新获得一个新的时间戳,这个时间戳肯定比当前的要大
            if (sequence == 0) {
                timeStamp = tilNextSeconds(lastTimeStamp);
            }
        }//如果时间戳大于上一个时间戳，则序列号从0开始计数
        else {
            sequence = 0L;
        }

        lastTimeStamp = timeStamp;

        //时间戳需要左移的位数
        Long timeStampShift = sequenceBits + diviceTypeBits;
        return (timeStamp<< timeStampShift | deviceTypeId << sequenceBits | sequence);

    }

    private Long tilNextSeconds(Long LastTimeStamp){
        long timestamp = getTimeStamp();
        while (timestamp <= LastTimeStamp) {
            timestamp = getTimeStamp();
        }
        return timestamp;
    }


}
