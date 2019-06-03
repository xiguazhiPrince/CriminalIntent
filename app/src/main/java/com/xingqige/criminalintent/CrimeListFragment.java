package com.xingqige.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xingqige.criminalintent.lab.CrimeLab;
import com.xingqige.criminalintent.model.Crime;
import com.xingqige.criminalintent.utils.CrimeItemTouchHelper;
import com.xingqige.criminalintent.utils.CrimeItemTouchHelperCallBack;

import java.util.Collections;
import java.util.List;

public class CrimeListFragment extends Fragment {

    private  RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private static final int REQUEST_CRIME = 1;

    private boolean mSubtitleVisible;

    private View view;

    private Callbacks mCallbacks;

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        handleRecyclerView();

        return view ;
    }

    @Override
    public void onResume() {
        super.onResume();

        handleRecyclerView();
    }

    private void handleRecyclerView() {
        List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();
        if (crimes == null || crimes.isEmpty()) {
            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.crime_linear);
            linearLayout.setVisibility(View.VISIBLE);
            mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
            mCrimeRecyclerView.setVisibility(View.GONE);

            ImageButton imageButton = (ImageButton) view.findViewById(R.id.crime_linear_imageButton);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Crime crime = new Crime();
                    CrimeLab.get(getActivity()).addCrime(crime);
                    Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getmId());
                    startActivity(intent);
                }
            });

        } else {
            mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
            mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            updateUI();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
//                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getmId());
//                startActivity(intent);
                updateUI();
                mCallbacks.onCrimeSelected(crime);

                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;


        private Crime mCrime;

        private void CrimeHolder(){
        }

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(mCrime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(getActivity(), mCrime.getTitle()+"被点击了", Toast.LENGTH_SHORT)
//                    .show();
//            Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getmId());
//
//            startActivityForResult(intent, REQUEST_CRIME);

//            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getmId());
//            startActivity(intent);

            mCallbacks.onCrimeSelected(mCrime);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME) {
            // Handle result
            Log.d("REQUEST_CRIME", "requestCode:"+requestCode+", resultCode:"+resultCode);
        }
    }

    public class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> implements CrimeItemTouchHelper {

        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public int getItemViewType(int position) {
            return mCrimes.get(position).getType();
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            CrimeHolder crimeHolder = new CrimeHolder(layoutInflater, viewGroup);
            return crimeHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            crimeHolder.bind(mCrimes.get(i));
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }

        @Override
        public void onItemMove(int fromPosition, int toPosition) {
            // 移动
        }

        @Override
        public void onItemDissmiss(int position) {
            // 删除
            Crime crime = mCrimes.get(position);
            CrimeLab.get(getActivity()).removeCrime(crime.getmId());
            notifyItemRemoved(position);
            updateUI();
        }
    }

    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null){
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();

        //先实例化Callback
        ItemTouchHelper.Callback callback = new CrimeItemTouchHelperCallBack(mAdapter);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(mCrimeRecyclerView);

    }

}
