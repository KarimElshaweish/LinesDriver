package com.example.linesdriver.Activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.linesdriver.Model.Driver;
import com.example.linesdriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.Country;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    EditText fullName,phoneNumber,state,area,near,password;
    Button btnSignUp;
    Driver driver;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    Boolean Login=false;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference mReference=database.getReference("Drivers");
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private Boolean mVerificationInProgress=false;
    CountryCodePicker ccp;
    String code;
    boolean codeSelected=false;
    private void __init__(){
        fullName=findViewById(R.id.fullName);
        phoneNumber=findViewById(R.id.phoneNumber);
        state=findViewById(R.id.state);
        area=findViewById(R.id.area);
        near=findViewById(R.id.near);
        password=findViewById(R.id.password);
        mAuth=FirebaseAuth.getInstance();
        btnSignUp=findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                valiadtion();
            }
        });
        ccp=findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected(Country selectedCountry) {
                code="+"+selectedCountry.getPhoneCode();
                codeSelected=true;
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        __init__();
    }
    public void finish(View view) {
        finish();
    }
    private void valiadtion(){
        if(fullName.getText().toString().equals("")){
            fullName.setError("please enter your full name");
        }else if(phoneNumber.getText().toString().equals("")){
            phoneNumber.setError("please enter your phone number");
        }else if(password.getText().toString().equals("")){
            password.setError("please enter your password");
        }else if(!codeSelected) {
            Toast.makeText(this, "please selected your country phone", Toast.LENGTH_SHORT).show();
        }else{
            driver=new Driver(fullName.getText().toString(),code+phoneNumber.getText().toString(),state.getText().toString(),area.getText().toString()
                    ,near.getText().toString(),password.getText().toString());
            startPhoneNumberVerification(driver.getPhoneNumber());
        }
    }
    private void startPhoneNumberVerification(final String phoneNumber) {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Auth", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("Auth", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                30L,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

        mVerificationInProgress = true;


    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Auth", "signInWithCredential:success");
                            if(!Login){
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                final UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(driver.getFullName())
                                        .build();
                                user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        driver.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        pushData();
                                    }
                                });}else{
                                startActivity(new Intent(getBaseContext(),MainActivity.class));
                            }
                        } else {
                            Log.w("Auth", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignUpActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    private void pushData(){
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mReference.child(driver.getId()).setValue(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        finish();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
