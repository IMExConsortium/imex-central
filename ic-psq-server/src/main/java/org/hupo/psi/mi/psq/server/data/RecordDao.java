package org.hupo.psi.mi.psq.server.data;

import java.util.List;

public interface RecordDao{
    public void addRecord( String id, String record, String format );
    public String getRecord( String id, String format );
    public List<String> getRecordList( List<String> id, String format );
}
