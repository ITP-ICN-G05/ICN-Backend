package com.gof.ICNBack.DataSources.Items;

import com.gof.ICNBack.Entity.Item;
import com.gof.ICNBack.Entity.Organisation;

import java.util.List;
import java.util.Map;

public abstract class ItemDao {
    public abstract Item getItemById(String organisationId);
}
