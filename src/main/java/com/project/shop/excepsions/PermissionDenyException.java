package com.project.shop.excepsions;

public class PermissionDenyException  extends  Exception{
    public PermissionDenyException(String message)
    {
        super(message);
    }

}
