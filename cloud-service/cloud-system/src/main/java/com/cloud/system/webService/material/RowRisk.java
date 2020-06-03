package com.cloud.system.webService.material;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "ROW")
public class RowRisk {
    private String MATERIAL_CODE;
    private String MATERIAL_DESCRITION;
    private String MATERIAL_TYPE;
    private String PRIMARY_UOM;
    private String MTL_GROUP_CODE;
    private String PLANT_CODE;
    private String PURCHASE_GROUP_CODE;
    private String ROUNDING_QUANTITY;
    private String LAST_UPD;

    public String getMATERIAL_CODE() {
        return MATERIAL_CODE;
    }

    public void setMATERIAL_CODE(String MATERIAL_CODE) {
        this.MATERIAL_CODE = MATERIAL_CODE;
    }

    public String getMATERIAL_DESCRITION() {
        return MATERIAL_DESCRITION;
    }

    public void setMATERIAL_DESCRITION(String MATERIAL_DESCRITION) {
        this.MATERIAL_DESCRITION = MATERIAL_DESCRITION;
    }

    public String getMATERIAL_TYPE() {
        return MATERIAL_TYPE;
    }

    public void setMATERIAL_TYPE(String MATERIAL_TYPE) {
        this.MATERIAL_TYPE = MATERIAL_TYPE;
    }

    public String getPRIMARY_UOM() {
        return PRIMARY_UOM;
    }

    public void setPRIMARY_UOM(String PRIMARY_UOM) {
        this.PRIMARY_UOM = PRIMARY_UOM;
    }

    public String getMTL_GROUP_CODE() {
        return MTL_GROUP_CODE;
    }

    public void setMTL_GROUP_CODE(String MTL_GROUP_CODE) {
        this.MTL_GROUP_CODE = MTL_GROUP_CODE;
    }

    public String getPLANT_CODE() {
        return PLANT_CODE;
    }

    public void setPLANT_CODE(String PLANT_CODE) {
        this.PLANT_CODE = PLANT_CODE;
    }

    public String getPURCHASE_GROUP_CODE() {
        return PURCHASE_GROUP_CODE;
    }

    public void setPURCHASE_GROUP_CODE(String PURCHASE_GROUP_CODE) {
        this.PURCHASE_GROUP_CODE = PURCHASE_GROUP_CODE;
    }

    public String getROUNDING_QUANTITY() {
        return ROUNDING_QUANTITY;
    }

    public void setROUNDING_QUANTITY(String ROUNDING_QUANTITY) {
        this.ROUNDING_QUANTITY = ROUNDING_QUANTITY;
    }

    public String getLAST_UPD() {
        return LAST_UPD;
    }

    public void setLAST_UPD(String LAST_UPD) {
        this.LAST_UPD = LAST_UPD;
    }
}
