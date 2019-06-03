package com.xingqige.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.xingqige.criminalintent.lab.CrimeLab;
import com.xingqige.criminalintent.model.Crime;
import com.xingqige.criminalintent.utils.PictureUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleField;
    private File mPhotoFile;

    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;


    private Button mSuspectButton;

    private Button mSuspectCall;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private View mCrimeFragmentView;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_IMAGE = "DialogImage";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_READE_CONTACT_PRESSION = 2;
    private static final int REQUEST_PHOTO= 3;
    private static final int REQUEST_IMAGE_VIEW= 4;

    private String mSuspect;
    private String mSuspectId;
    private String mSuspectPhone;

    private Callbacks mCallbacks;

    public interface Callbacks{
        void onCrimeUpdated(Crime crime);
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

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mCrime = new Crime();

//        UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        Bundle arguments = getArguments();
        UUID crimeId = (UUID)arguments.getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCrimeFragmentView = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)mCrimeFragmentView.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) mCrimeFragmentView.findViewById(R.id.crime_date);
        updateDate();
//        mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)mCrimeFragmentView.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });

        mReportButton = (Button) mCrimeFragmentView.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(getActivity());
                intentBuilder.setType("text/plain");
                intentBuilder.setText(getCrimeReport());
                intentBuilder.setSubject(getString(R.string.crime_report_subject));
                intentBuilder.createChooserIntent();
                intentBuilder.startChooser();

//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
//                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
//
//                Intent.createChooser(intent, getString(R.string.send_report));
//                startActivity(intent);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)mCrimeFragmentView.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null){
            mSuspectButton.setText(mCrime.getSuspect());
        }
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mSuspectCall = (Button)mCrimeFragmentView.findViewById(R.id.crime_call);
        mSuspectCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                查询 ContactsContract 数据库中的 CommonDataKinds.Phone 表
                getPhone(mSuspectId, 1);
                if (mSuspectPhone != null){
                    Uri number = Uri.parse("tel:"+mSuspectPhone+"");
                    Intent intent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(intent);
                }
            }
        });

        mPhotoButton = (ImageButton) mCrimeFragmentView.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) mCrimeFragmentView.findViewById(R.id.crime_photo);

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.xingqige.criminalintent.fileprovider", mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageViewFragment imageViewFragment = ImageViewFragment.newInstance(mPhotoFile);

                FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
                imageViewFragment.setTargetFragment(CrimeFragment.this, REQUEST_IMAGE_VIEW);
                imageViewFragment.show(manager, DIALOG_IMAGE);
            }
        });

        updatePhotoView();
        return mCrimeFragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateCrime();
            updateDate();
        }

        if (requestCode == REQUEST_CONTACT && data != null){
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for.
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER,
                    ContactsContract.Contacts._ID
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try{
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }
                // Pull out the first column of the first row of data -
                // that is your suspect's name
                c.moveToFirst();
                mSuspect = c.getString(0);
                int hasNumber = c.getInt(1);
                mSuspectId = c.getString(2);
                mCrime.setSuspect(mSuspect);
                updateCrime();
                mSuspectButton.setText(mSuspect);

                //判断用户是否已经授权给我们了 如果没有，调用下面方法向用户申请授权，之后系统就会弹出一个权限申请的对话框
                if (ContextCompat.checkSelfPermission(getActivity().getApplication() ,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS},REQUEST_READE_CONTACT_PRESSION);
                } else {
                    getPhone(mSuspectId, hasNumber);
                }
            }finally {
                c.close();
            }
        }

        if (REQUEST_PHOTO == requestCode ){
            Log.d("REQUEST_PHOTO", "234");
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.xingqige.criminalintent.fileprovider", mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updateCrime();
            updatePhotoView();
        }

    }

    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }

    private void getPhone(String mSuspectId, int hasNumber) {
        if (hasNumber>0){
            ContentResolver resolver = getActivity().getContentResolver();

            String[] phoneProjection = new String[] {
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            Cursor cursor = resolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    phoneProjection,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + mSuspectId,
                    null,
                    null);
            try {
                if (cursor != null) {
                    cursor.moveToFirst();
                        //注意 如果某个联系人有多个电话 这里会出现多条 rawId相同 但是phone不同的数据
                    String string = cursor.getString(0);
                    Pattern p = Pattern.compile("[^0-9]");
                    Matcher m = p.matcher(string);
                    mSuspectPhone = m.replaceAll("");
                }
            } finally {
                cursor.close();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String  getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = (String)DateFormat.format(dateFormat, mCrime.getDate());
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap scaledBitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(scaledBitmap);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_menu_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove_crime:
                CrimeLab.get(getActivity()).removeCrime(mCrime.getmId());
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //回调方法，无论哪种结果，最终都会回调该方法，之后在判断用户是否授权，
    // 用户同意则调用readContacts（）方法，失败则会弹窗提示失败
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READE_CONTACT_PRESSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getPhone(mSuspectId, 1);
                } else {
                    Toast.makeText(getActivity(), "获取联系人权限失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
