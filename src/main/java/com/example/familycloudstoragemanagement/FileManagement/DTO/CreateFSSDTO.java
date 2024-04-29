package com.example.familycloudstoragemanagement.FileManagement.DTO;

import com.qiwenshare.common.constant.RegexConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@Schema(name = "创建家庭共享空间DTO",required = true)
public class CreateFSSDTO {
    @Schema(description="家庭共享空间名", required=true)
    @NotBlank(message = "家庭名不能为空")
    private String FSSName;

}
