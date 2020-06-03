package com.cloud.system.webService.material;

import cn.hutool.core.util.XmlUtil;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
@XmlRootElement(name = "OUTPUT")
public class ProcessResponse implements java.io.Serializable{
    private List<RowRisk> ROWSET = new ArrayList<RowRisk>();

    public List<RowRisk> getROWSET() {
        return ROWSET;
    }

    public void setROWSET(List<RowRisk> ROWSET) {
        this.ROWSET = ROWSET;
    }

    public void addROW(RowRisk ROW) {
        this.ROWSET.add(ROW);
    }
}
