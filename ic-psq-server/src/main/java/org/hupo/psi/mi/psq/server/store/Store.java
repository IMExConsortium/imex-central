package org.hupo.psi.mi.psq.server.store;

import java.util.List;

public interface Store{
    public String getRecord( String id, String format );
    public List<String> getRecordList( List<String> id, String format );
}
