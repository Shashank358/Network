package com.ssproduction.shashank.network.Fragments;


import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.ssproduction.shashank.network.Fragments.BottomSheets.SendImgBottomSheet;
import com.ssproduction.shashank.network.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsOptionFragment extends Fragment {

    private ImageView sendImage;


    public ChatsOptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats_option, container, false);

        sendImage = (ImageView) view.findViewById(R.id.chat_option_send_image);

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View view1 = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog_chats, null);
                BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
                dialog.setContentView(view1);
                dialog.show();

            }
        });

        return view;
    }

}
