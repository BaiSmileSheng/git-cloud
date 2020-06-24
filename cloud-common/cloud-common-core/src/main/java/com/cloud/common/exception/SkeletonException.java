package com.cloud.common.exception;

import com.cloud.common.enums.StatusEnums;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 全局异常
 *
 * @author cs
 * @Description
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SkeletonException extends RuntimeException {

    private StatusEnums statusEnums;
}
