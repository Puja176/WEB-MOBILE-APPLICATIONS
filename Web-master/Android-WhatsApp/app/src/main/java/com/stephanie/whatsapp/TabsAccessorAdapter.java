package com.stephanie.whatsapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAccessorAdapter extends FragmentPagerAdapter{

    public TabsAccessorAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int number) {

        switch (number) {
            case 0:
                return new ChatsFragment();

            case 1:
                return new GroupsFragment();

            case 2:
                return new ContactsFragment();

            case 3:
                return new RequestFragment();

            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int ranking) {
        switch (ranking) {
            case 0:
                return "Chats";

            case 1:
                return "Groups";

            case 2:
                return "Contacts";

            case 3:
                return "Requests";

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
