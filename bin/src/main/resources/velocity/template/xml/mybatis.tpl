<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${model}NS">
    <resultMap type="${basePackage}.model.${model}" id="${model}Id">
    #foreach($entry in $columnMapFields.entrySet())
    	<result column="$entry.key" property="$entry.value" jdbcType = "$dbTypes.get($entry.key)"/>
    #end
	</resultMap> 

    <select id="selectEntity${model}" resultMap="${model}Id" parameterType="${basePackage}.model.${model}">
        select * from ${table}
        where <include refid="dynamic_where" />
    </select>

    <select id="selectEntityCond${model}" resultMap="${model}Id" parameterType="java.util.Map">
        select * from ${table}
        <if test="cond!=null and cond.size() > 0">
       		where
        	<include refid="dynamic_cond" />
            <if test="cond.orderby != null">
            	order by ${cond.orderby}
            </if>
        </if>
    </select>

    <select id="selectTotalEntityCond${model}" resultType="Integer" parameterType="java.util.Map">
        select count(0) from ${table}
        where
            <include refid="dynamic_where" />
    </select>

    <delete id="deleteEntity${model}" parameterType="${basePackage}.model.${model}">
        update ${table}
           set status=2,update_time=sysdate
       	where
            <include refid="dynamic_where" />
    </delete>

    <insert id="insertEntity${model}" parameterType="${basePackage}.model.${model}">
        insert into ${table}(
        #foreach($entry in $columnMapFields.entrySet())
			#if (!$insertDefault.containsKey($entry.key))
			  <if test="$entry.value != null">
			  	$entry.key,
			  </if>
			#end
        #end
        #foreach($entry in $insertDefault.entrySet())
        	#if ($velocityCount != $insertDefault.size())
				$entry.key,
			#else
				$entry.key
			#end
        #end
        )
        values
        (
        #foreach($entry in $columnMapFields.entrySet())
			#if (!$insertDefault.containsKey($entry.key))
			  <if test="$entry.value != null">
			  	#{$entry.value,jdbcType=$dbTypes.get($entry.key)},
			  </if>
			#end
        #end
        #foreach($entry in $insertDefault.entrySet())
        	#if ($velocityCount != $insertDefault.size())
				$entry.value,
			#else
				$entry.value
			#end
        #end
        )
    </insert> 
    
	<update id="updateEntity${model}" parameterType="java.util.Map">
        update ${table}
        <set>
        #foreach($entry in $columnMapFields.entrySet())
            #if (!$insertDefault.containsKey($entry.key))
	            <if test="newvalue.$entry.value != null">
	                $entry.key=#{newvalue.$entry.value, jdbcType=$dbTypes.get($entry.key)},
	            </if>
            #end
        #end
        #foreach($entry in $updateDefault.entrySet())
        	#if ($velocityCount != $updateDefault.size())
				$entry.key = $entry.value,
			#else
				$entry.key = $entry.value
        	#end
        #end
        </set>
        where
            <include refid="dynamic_cond" />
    </update>
    
    <sql id="dynamic_where">
        <trim prefixOverrides="and" suffixOverrides="and">
        #foreach($entry in $columnMapFields.entrySet())
	        <if test="$entry.value != null">
	            and $entry.key=#{$entry.value, jdbcType=$dbTypes.get($entry.key)}
	        </if>
        #end
        </trim>
    </sql>

    <sql id="dynamic_cond">
        <trim prefixOverrides="and" suffixOverrides="and">
        #foreach($entry in $columnMapFields.entrySet())
	        <if test="cond.$entry.value != null">
	            and $entry.key=#{cond.$entry.value, jdbcType=$dbTypes.get($entry.key)}
	        </if>
	        #if ($dbTypes.get($entry.key) == "TIMESTAMP")
	            <if test="cond.${entry.value}Lower != null">
	            	and ${entry.key} &gt;=#{cond.${entry.value}Lower, jdbcType=$dbTypes.get($entry.key)}
	            </if>
	        #end
	       	#if ($dbTypes.get($entry.key) == "TIMESTAMP")
	            <if test="cond.${entry.value}Lower != null">
	            	and ${entry.key} &lt;=#{cond.${entry.value}Upper, jdbcType=$dbTypes.get($entry.key)}
	            </if>
	        #end
	       	#if ($dbTypes.get($entry.key) == "TIMESTAMP")
	            <if test="cond.${entry.value}Trunc != null">
	            	and trunc(${entry.key},'dd') =trunc(#{cond.${entry.value}Lower, jdbcType=$dbTypes.get($entry.key)},'dd')
	            </if>
	        #end
        #end
        </trim>
    </sql>
</mapper>