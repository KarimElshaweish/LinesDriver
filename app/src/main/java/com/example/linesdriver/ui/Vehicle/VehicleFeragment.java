package com.example.linesdriver.ui.Vehicle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.example.linesdriver.Model.Driver;
import com.example.linesdriver.Model.ProfilePicture;
import com.example.linesdriver.Model.Vechicel;
import com.example.linesdriver.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VehicleFeragment extends Fragment {


    LoaderTextView fullName;
    Button updateBtn;
    EditText licencesNumber;
    CircleImageView pc1,pc2,pc3,avatar;
    int SELECT_IMAGE;
    List<String>files;
    Boolean edit=false;
    List<Uri>uriList;
    List<String>fileNameList;
    private StorageReference mStorage;
    RelativeLayout add1,add2,add3;
    FloatingActionButton changePic;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.vehicle_feragment, container, false);
        __ini__(root);
        getProfileData();
        return root;
    }

    private void __ini__(View root) {
        avatar=root.findViewById(R.id.avatar);
        changePic=root.findViewById(R.id.changePic);
        changePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SELECT_IMAGE=77;
                openGallery();
            }
        });
        add1=root.findViewById(R.id.add1);
        add2=root.findViewById(R.id.add2);
        add3=root.findViewById(R.id.add3);
        mStorage = FirebaseStorage.getInstance().getReference();
        files=new ArrayList<>();
        pc1=root.findViewById(R.id.pc1);
        pc2=root.findViewById(R.id.pc2);
        pc3=root.findViewById(R.id.pc3);
        pc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit) {
                    openGallery();
                    SELECT_IMAGE = 3;
                }
            }
        });
        pc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit){
                openGallery();
                SELECT_IMAGE=2;
                }
            }
        });
        pc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit) {
                    SELECT_IMAGE = 1;
                    openGallery();
                }
            }
        });
        fullName=root.findViewById(R.id.fullName);
        fullName.resetLoader();
        updateBtn=root.findViewById(R.id.updateBtn);
        licencesNumber=root.findViewById(R.id.licencesNumber);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btnText=updateBtn.getText().toString();
                if(btnText.equals("Save")){
                    desiableChangePic();
                    editFireBase();
                    updateBtn.setText("Edit");
                    edit=false;
                    changeUIForSaving();
                }else {
                    enableChangePic();
                    uriList=new ArrayList<>();
                    fileNameList=new ArrayList<>();
                    licencesNumber.setEnabled(true);
                    updateBtn.setText("Save");
                    changeUIForEdit();
                    edit=true;
                }
            }
        });
        getData();
    }

    @SuppressLint("RestrictedApi")
    private void enableChangePic() {
        changePic.setVisibility(View.GONE);
    }

    @SuppressLint("RestrictedApi")
    private void desiableChangePic() {
        changePic.setVisibility(View.VISIBLE);
    }

    List<String>downloadList;
    private void editFireBase() {
        downloadList=new ArrayList<>();
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Updating");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        for(int i=0;i<uriList.size();i++){
            Uri uri=uriList.get(i);
            final StorageReference fileUpload = mStorage.child("Vehicles").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(fileNameList.get(i));
            final int finalI = i;
            fileUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadList.add(uri.toString());
                            if(finalI==uriList.size()-1){
                                Vechicel vechicel=new Vechicel(licencesNumber.getText().toString(),downloadList,FirebaseAuth.getInstance().getCurrentUser().getUid());
                                FirebaseDatabase.getInstance().getReference("Vechicle").child(vechicel.getDriverID())
                                        .setValue(vechicel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }
    }

    private void getProfileData(){
        FirebaseDatabase.getInstance().getReference("Drivers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Driver driver=dataSnapshot.getValue(Driver.class);
                        fullName.setText(driver.getFullName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
    }
    int last=-1;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((last==-1&&requestCode == SELECT_IMAGE)||((last==requestCode?requestCode+=1:requestCode)==SELECT_IMAGE)) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        Uri uri = data.getData();
                        if(SELECT_IMAGE!=77) {
                            last = SELECT_IMAGE;
                            uriList.add(uri);
                            File file = new File(uri.getPath());
                            String fileName = file.getName();
                            fileNameList.add(fileName);
                        }
                        switch (SELECT_IMAGE){
                            case 1:
                                pc1.setImageBitmap(bitmap);
                                break;
                            case 2:
                                pc2.setImageBitmap(bitmap);
                                break;
                            case 3:
                                pc3.setImageBitmap(bitmap);
                                break;
                            case 77:
                                avatar.setImageBitmap(bitmap);
                                uploadProfilePicture(uri);
                                break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadProfilePicture(Uri uri) {
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setTitle("Updating profile Pricture");
        progressDialog.show();
        final StorageReference fileUpload = mStorage.child("Profiles").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        fileUpload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(uri)
                                .build();
                        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ProfilePicture profilePicture=new ProfilePicture();
                                profilePicture.setUrl(uri.toString());
                                FirebaseDatabase.getInstance().getReference("Profiles")
                                        .child(user.getUid())
                                        .setValue(profilePicture).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData(){
        Picasso.get().load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl())
                .into(avatar);
        FirebaseDatabase.getInstance().getReference("Vechicle")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null) {
                        Vechicel vechicel = dataSnapshot.getValue(Vechicel.class);
                        updaeUI(vechicel);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updaeUI(Vechicel vechicel) {
        licencesNumber.setText(vechicel.getLicences());
        int size=vechicel.getUriList().size();
        switch (size){
            case 1:
                Picasso.get().load(vechicel.getUriList().get(0)).placeholder(R.drawable.bus).into(pc1);
                break;
            case 2:
                Picasso.get().load(vechicel.getUriList().get(0)).placeholder(R.drawable.bus).into(pc2);
                Picasso.get().load(vechicel.getUriList().get(1)).placeholder(R.drawable.bus).into(pc2);
                break;
            case 3:
                Picasso.get().load(vechicel.getUriList().get(0)).placeholder(R.drawable.bus).into(pc1);
                Picasso.get().load(vechicel.getUriList().get(1)).placeholder(R.drawable.bus).into(pc2);
                Picasso.get().load(vechicel.getUriList().get(2)).placeholder(R.drawable.bus).into(pc3);
                break;
        }
    }
    private void changeUIForEdit(){
        add1.setVisibility(View.VISIBLE);
        add3.setVisibility(View.VISIBLE);
        add2.setVisibility(View.VISIBLE);
    }
    private void changeUIForSaving(){
        add1.setVisibility(View.GONE);
        add3.setVisibility(View.GONE);
        add2.setVisibility(View.GONE);
    }
}