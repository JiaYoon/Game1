package com.example.ga.rps.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.ga.rps.MainActivity;
import com.example.ga.rps.R;

/**
 * Created by GA on 2018. 4. 13..
 */

public class ModeChangeFragment extends Fragment {

    public static ModeChangeFragment getInstance(int res) {
        ModeChangeFragment modeChangeFragment = new ModeChangeFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("resId", res);

        modeChangeFragment.setArguments(bundle);
        return modeChangeFragment;
    }

    private int resId;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        resId = getArguments().getInt("resId");
        view = inflater.inflate(resId, container, false);
        view.findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() == null) return;
                if (resId == R.layout.fragment_mode_selection_1) {
                    Toast.makeText(getContext(), "앞뒤는 준비중입니다 ㅠㅠ", Toast.LENGTH_SHORT).show();
                    return;
                }
                ((MainActivity) getActivity()).showCameraPage();
            }
        });

        return view;
    }

    public void startAnimation() {
        if (resId == R.layout.fragment_mode_selection_2) {
            view.findViewById(R.id.deco_hand1).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_left_top_deco));
            view.findViewById(R.id.deco_hand2).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_top_deco));
            view.findViewById(R.id.deco_hand3).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_top_deco));
            view.findViewById(R.id.deco_hand4).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_bottom_deco));
            view.findViewById(R.id.deco_hand5).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_bottom_deco));
            view.findViewById(R.id.deco_hand6).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_bottom_deco));
            view.findViewById(R.id.deco_hand7).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.translate_left_bottom_deco));
        } else {
            view.findViewById(R.id.deco_hand1).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_left_top_deco));
            view.findViewById(R.id.deco_hand2).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_left_top_deco));
            view.findViewById(R.id.deco_hand3).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_right_top_deco));
            view.findViewById(R.id.deco_hand4).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_right_top_deco));
            view.findViewById(R.id.deco_hand5).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_right_bottom_deco));
            view.findViewById(R.id.deco_hand7).startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.scale_left_bottom_deco));
        }

    }
}
