package id.co.veritrans.sdk.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import id.co.veritrans.sdk.R;
import id.co.veritrans.sdk.activities.SaveCreditCardActivity;
import id.co.veritrans.sdk.core.Constants;
import id.co.veritrans.sdk.core.Logger;
import id.co.veritrans.sdk.core.SdkUtil;
import id.co.veritrans.sdk.core.VeritransSDK;
import id.co.veritrans.sdk.models.BankDetail;
import id.co.veritrans.sdk.models.UserDetail;
import id.co.veritrans.sdk.widgets.VeritransDialog;

/**
 * @author rakawm
 */
public class RegisterCardFragment extends Fragment {

    private String lastExpDate = "";
    private EditText etCardNo;
    private EditText etCvv;
    private EditText etExpiryDate;
    private CheckBox cbStoreCard;
    private ImageView questionImg;
    private ImageView questionSaveCardImg;
    private Button saveBtn;
    private String cardNumber;
    private String cvv;
    private String expiryDate;
    private String[] expDateArray;
    private int expMonth;
    private int expYear;
    private VeritransSDK veritransSDK;
    private UserDetail userDetail;
    private ArrayList<BankDetail> bankDetails;
    private String cardType = "";
    private RelativeLayout formLayout;

    public static RegisterCardFragment newInstance() {
        return new RegisterCardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SaveCreditCardActivity) getActivity()).getTitleHeaderTextView().setText(getString(R.string.card_details));
        veritransSDK = ((SaveCreditCardActivity) getActivity()).getVeritransSDK();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_card_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            Logger.i("onViewCreated called addcarddetail called");
            SaveCreditCardActivity creditCardActivity = (SaveCreditCardActivity) getActivity();
            if (creditCardActivity != null) {
                if (creditCardActivity.getSupportActionBar() != null) {
                    creditCardActivity.getSupportActionBar().setTitle(getString(R.string.card_details));
                    creditCardActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        bindViews(view);
        ((SaveCreditCardActivity) getActivity()).getBtnMorph().setVisibility(View.GONE);
        fadeIn();
        super.onViewCreated(view, savedInstanceState);
    }

    public void focusChange() {
        Logger.i("onFocus change has not focus");
        isValid(false);
    }

    private void bindViews(View view) {
        formLayout = (RelativeLayout) view.findViewById(R.id.form_layout);
        etCardNo = (EditText) view.findViewById(R.id.et_card_no);
        etCvv = (EditText) view.findViewById(R.id.et_cvv);
        etExpiryDate = (EditText) view.findViewById(R.id.et_exp_date);
        cbStoreCard = (CheckBox) view.findViewById(R.id.cb_store_card);
        cbStoreCard.setVisibility(View.GONE);
        questionImg = (ImageView) view.findViewById(R.id.image_question);
        questionSaveCardImg = (ImageView) view.findViewById(R.id.image_question_save_card);
        questionSaveCardImg.setVisibility(View.GONE);
        saveBtn = (Button) view.findViewById(R.id.btn_pay_now);
        saveBtn.setText(R.string.btn_save_card);
        //formLayout.setAlpha(0);
        etCardNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Logger.i("onFocus change etCardNo");
                if (!hasFocus) {
                    Logger.i("onFocus change not etCardNo");
                    focusChange();
                } else {
                    Logger.i("onFocus change has focus etCardNo");
                }
            }
        });
        etCvv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Logger.i("onFocus change etCvv");
                if (!hasFocus) {
                    Logger.i("onFocus change not etCvv");
                    focusChange();
                } else {
                    Logger.i("onFocus change has focus etCvv");
                }
            }
        });
        etExpiryDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Logger.i("onFocus change etExpiryDate");
                if (!hasFocus) {
                    Logger.i("onFocus change not etExpiryDate");
                    focusChange();
                } else {
                    Logger.i("onFocus change has focus etExpiryDate");
                }
            }
        });
        cbStoreCard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                isValid(false);
            }
        });
        if (veritransSDK != null && veritransSDK.getSemiBoldText() != null) {
            saveBtn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), veritransSDK.getSemiBoldText()));
        }
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid(true)) {
                    // Save card
                    SdkUtil.showProgressDialog(getActivity(), false);
                    SaveCreditCardActivity activity = (SaveCreditCardActivity) getActivity();
                    String date = etExpiryDate.getText().toString();
                    String month = date.split("/")[0];
                    String year = "20" + date.split("/")[1];
                    if (activity != null) {
                        activity.registerCard(cardNumber, cvv, month, year);
                    }
                }
            }
        });
        questionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VeritransDialog veritransDialog = new VeritransDialog(getActivity(), getResources().getDrawable(R.drawable.cvv_dialog_image),
                        getString(R.string.message_cvv), getString(R.string.got_it), "");
                veritransDialog.show();
            }
        });
        questionSaveCardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VeritransDialog veritransDialog = new VeritransDialog(getActivity(), getResources().getDrawable(R.drawable.cart_dialog),
                        getString(R.string.message_save_card), getString(R.string.got_it), "");
                veritransDialog.show();
            }
        });

        etCardNo.addTextChangedListener(new TextWatcher() {
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf
                            (space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
                setCardType();
            }
        });

        etExpiryDate.addTextChangedListener(new

                                                    TextWatcher() {
                                                        @Override
                                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                        }

                                                        @Override
                                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                        }

                                                        @Override
                                                        public void afterTextChanged(Editable s) {
                                                            String input = s.toString();
                                                            if (s.length() == 2 && !lastExpDate.endsWith("/")) {
                                                                int month = Integer.parseInt(input);
                                                                if (month <= 12) {
                                                                    etExpiryDate.setText(etExpiryDate.getText().toString() + "/");
                                                                    etExpiryDate.setSelection(etExpiryDate.getText().toString().length());
                                                                } else {
                                                                    etExpiryDate.setText(Constants.MONTH_COUNT + "/");
                                                                    etExpiryDate.setSelection(etExpiryDate.getText().toString().length());
                                                                }
                                                            } else if (s.length() == 2 && lastExpDate.endsWith("/")) {
                                                                int month = Integer.parseInt(input);
                                                                if (month <= 12) {
                                                                    etExpiryDate.setText(etExpiryDate.getText().toString().substring(0, 1));
                                                                    etExpiryDate.setSelection(etExpiryDate.getText().toString().length());
                                                                } else {
                                                                    etExpiryDate.setText("");
                                                                    etExpiryDate.setSelection(etExpiryDate.getText().toString().length());
                                                                }
                                                            } else if (s.length() == 1) {
                                                                int month = Integer.parseInt(input);
                                                                if (month > 1) {
                                                                    etExpiryDate.setText("0" + etExpiryDate.getText().toString() + "/");
                                                                    etExpiryDate.setSelection(etExpiryDate.getText().toString().length());
                                                                }
                                                            }
                                                            lastExpDate = etExpiryDate.getText().toString();
                                                        }
                                                    }

        );
    }

    private boolean isValid(boolean isPayButtonClick) {
        cardNumber = etCardNo.getText().toString().trim().replace(" ", "");
        expiryDate = etExpiryDate.getText().toString().trim();
        cvv = etCvv.getText().toString().trim();
        questionImg.setVisibility(View.VISIBLE);
        try {
            expDateArray = expiryDate.split("/");
            Logger.i("expDate:" + expDateArray[0], "" + expDateArray[1]);
        } catch (NullPointerException e) {
            Logger.i("expiry date empty");
        } catch (IndexOutOfBoundsException e) {
            Logger.i("expiry date issue");
        }
        if (TextUtils.isEmpty(cardNumber)) {
            if (isPayButtonClick) {
                SdkUtil.showSnackbar(getActivity(), getString(R.string.validation_message_card_number));
                // etCardNo.requestFocus();
            }
            if (!etCardNo.hasFocus()) {
                etCardNo.setError(getString(R.string.validation_message_card_number));
            }
            /*etExpiryDate.clearFocus();
            etCvv.clearFocus();*/
            return false;
        } else if (cardNumber.length() < 16 || !SdkUtil.isValidCardNumber(cardNumber)) {
            /*etCardNo.requestFocus();
            etExpiryDate.clearFocus();
            etCvv.clearFocus();*/
            if (!etCardNo.hasFocus()) {
                etCardNo.setError(getString(R.string.validation_message_invalid_card_no));
            }
            if (isPayButtonClick) {
                SdkUtil.showSnackbar(getActivity(), getString(R.string
                        .validation_message_invalid_card_no));
            }
            return false;
        } else if (TextUtils.isEmpty(expiryDate)) {
            /*etCardNo.clearFocus();
            etCvv.clearFocus();
            etExpiryDate.requestFocus();*/

            if (isPayButtonClick) {
                SdkUtil.showSnackbar(getActivity(), getString(R.string
                        .validation_message_empty_expiry_date));
            }
            if (!etExpiryDate.hasFocus()) {
                etExpiryDate.setError(getString(R.string.validation_message_empty_expiry_date));
            }
            return false;
        } else if (!expiryDate.contains("/")) {
            /*etCardNo.clearFocus();
            etCvv.clearFocus();
            etExpiryDate.requestFocus();*/
            if (isPayButtonClick) {
                SdkUtil.showSnackbar(getActivity(), getString(R.string
                        .validation_message_invalid_expiry_date));
            }
            if (!etExpiryDate.hasFocus()) {
                etExpiryDate.setError(getString(R.string.validation_message_invalid_expiry_date));
            }
            return false;
        } else if (expDateArray == null || expDateArray.length != 2) {
            /*etCardNo.clearFocus();
            etCvv.clearFocus();
            etExpiryDate.requestFocus();*/
            if (isPayButtonClick) {
                SdkUtil.showSnackbar(getActivity(), getString(R.string
                        .validation_message_invalid_expiry_date));
            }
            if (!etExpiryDate.hasFocus()) {
                etExpiryDate.setError(getString(R.string.validation_message_invalid_expiry_date));
            }
            return false;
        } else if (expDateArray != null) {
            try {
                expMonth = Integer.parseInt(expDateArray[0]);
            } catch (NumberFormatException e) {
               /* etCardNo.clearFocus();
                etCvv.clearFocus();
                etExpiryDate.requestFocus();*/
                if (isPayButtonClick) {
                    SdkUtil.showSnackbar(getActivity(), getString(R.string
                            .validation_message_invalid_expiry_date));
                }
                if (!etExpiryDate.hasFocus()) {
                    etExpiryDate.setError(getString(R.string.validation_message_invalid_expiry_date));
                }
                return false;
            }
            try {
                expYear = Integer.parseInt(expDateArray[1]);
            } catch (NumberFormatException e) {
              /*  etCardNo.clearFocus();
                etCvv.clearFocus();
                etExpiryDate.requestFocus();*/
                if (isPayButtonClick) {
                    SdkUtil.showSnackbar(getActivity(), getString(R.string
                            .validation_message_invalid_expiry_date));
                }
                if (!etExpiryDate.hasFocus()) {
                    etExpiryDate.setError(getString(R.string.validation_message_invalid_expiry_date));
                }
                return false;
            }
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yy");
            String year = format.format(date);

            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentYear = Integer.parseInt(year);
            Logger.i("currentMonth:" + currentMonth + ",currentYear:" + currentYear);
            if (expYear < currentYear) {
              /*  etCardNo.clearFocus();
                etCvv.clearFocus();
                etExpiryDate.requestFocus();*/
                if (isPayButtonClick) {
                    SdkUtil.showSnackbar(getActivity(), getString(R.string
                            .validation_message_invalid_expiry_date));
                }
                if (!etExpiryDate.hasFocus()) {
                    etExpiryDate.setError(getString(R.string.validation_message_invalid_expiry_date));
                }
                return false;
            } else if (expYear == currentYear && currentMonth > expMonth) {
             /*   etCardNo.clearFocus();
                etCvv.clearFocus();
                etExpiryDate.requestFocus();*/
                if (isPayButtonClick) {
                    SdkUtil.showSnackbar(getActivity(), getString(R.string
                            .validation_message_invalid_expiry_date));
                }
                if (!etExpiryDate.hasFocus()) {
                    etExpiryDate.setError(getString(R.string.validation_message_invalid_expiry_date));
                }
                return false;
            } else if (TextUtils.isEmpty(cvv)) {
                /*etCardNo.clearFocus();
                etCvv.requestFocus();
                etExpiryDate.clearFocus();*/
                if (isPayButtonClick) {
                    SdkUtil.showSnackbar(getActivity(), getString(R.string.validation_message_cvv));
                }
                if (!etCvv.hasFocus()) {
                    etCvv.setError(getString(R.string.validation_message_cvv));
                }
                questionImg.setVisibility(View.GONE);
                return false;
            } else {
                if (cvv.length() < 3) {
                  /*  etCardNo.clearFocus();
                    etCvv.requestFocus();
                    etExpiryDate.clearFocus();*/
                    if (isPayButtonClick) {
                        SdkUtil.showSnackbar(getActivity(), getString(R.string
                                .validation_message_invalid_cvv));
                    }
                    if (!etCvv.hasFocus()) {
                        etCvv.setError(getString(R.string.validation_message_invalid_cvv));
                    }
                    questionImg.setVisibility(View.GONE);
                    return false;
                }
                questionImg.setVisibility(View.VISIBLE);
                /*etCvv.clearFocus();
                cbStoreCard.requestFocus();*/
            }

        }
        return true;
    }

    private void setCardType() {
        String cardNo = etCardNo.getText().toString().trim();
        if (cardNo.isEmpty() || cardNo.length() < 2) {
            etCardNo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            return;
        }
        if (cardNo.charAt(0) == '4') {
            Drawable visa = getResources().getDrawable(R.drawable.visa_non_transperent);
            etCardNo.setCompoundDrawablesWithIntrinsicBounds(null, null, visa, null);
            cardType = getString(R.string.visa);
        } else if ((cardNo.charAt(0) == '5') && ((cardNo.charAt(1) == '1') || (cardNo.charAt(1) == '2')
                || (cardNo.charAt(1) == '3') || (cardNo.charAt(1) == '4') || (cardNo.charAt(1) == '5'))) {
            Drawable masterCard = getResources().getDrawable(R.drawable.mastercard_non_transperent);
            etCardNo.setCompoundDrawablesWithIntrinsicBounds(null, null, masterCard, null);
            cardType = getString(R.string.mastercard);

        } else if ((cardNo.charAt(0) == '3') && ((cardNo.charAt(1) == '4') || (cardNo.charAt(1) == '7'))) {
            Drawable amex = getResources().getDrawable(R.drawable.amex_non_transperent);
            etCardNo.setCompoundDrawablesWithIntrinsicBounds(null, null, amex, null);
            cardType = "AMEX";

        } else {
            cardType = "";

        }
    }

    private void fadeIn() {
        formLayout.setAlpha(0);
        ObjectAnimator fadeInAnimation = ObjectAnimator.ofFloat(formLayout, "alpha", 0, 1f);
        fadeInAnimation.setDuration(Constants.FADE_IN_FORM_TIME);
        fadeInAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                saveBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        fadeInAnimation.start();
    }
}
