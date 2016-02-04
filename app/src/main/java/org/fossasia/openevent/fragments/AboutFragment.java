package org.fossasia.openevent.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.fossasia.openevent.R;

/**
 * Created by duncanleo on 2/2/16.
 */
public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        RelativeLayout aboutNotifMode = (RelativeLayout)v.findViewById(R.id.about_notification_mode);
        LinearLayout aboutWebsite = (LinearLayout)v.findViewById(R.id.about_website);
        LinearLayout aboutTwitter = (LinearLayout)v.findViewById(R.id.about_twitter);
        LinearLayout aboutRate = (LinearLayout)v.findViewById(R.id.about_rate);
        LinearLayout aboutSubscribe = (LinearLayout)v.findViewById(R.id.about_subscribe);

        //Click handlers
        aboutWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://2016.fossasia.com"));
                startActivity(i);
            }
        });

        aboutTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=@fossasia"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/fossasia"));
                }
                startActivity(intent);
            }
        });

        return v;
    }
}
