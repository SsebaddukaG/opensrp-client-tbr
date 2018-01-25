package org.smartregister.tbr.helper.view;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.smartregister.tbr.R;
import org.smartregister.tbr.model.Contact;
import org.smartregister.tbr.util.Constants;
import org.smartregister.tbr.util.Utils;

/**
 * Created by ndegwamartin on 22/12/2017.
 */

public class ScreenContactViewHelper {
    private FrameLayout frameLayout;
    private static String TAG = RenderContactScreeningCardHelper.class.getCanonicalName();

    public ScreenContactViewHelper(final Context context, View frameView, Contact screenContactData) {
        //start with the frame
        FrameLayout frameLayoutTemplate = (FrameLayout) frameView.findViewById(R.id.clientContactFrameLayout);
        if (frameLayoutTemplate != null) {
            frameLayout = new FrameLayout(context);
            frameLayout.setLayoutParams(frameLayoutTemplate.getLayoutParams());
            frameLayout.setPadding(frameLayoutTemplate.getPaddingLeft(), frameLayoutTemplate.getPaddingTop(), frameLayoutTemplate.getPaddingRight(), frameLayoutTemplate.getPaddingBottom());
            frameLayout.setId(View.generateViewId());
            frameLayout.setTag(R.id.CONTACT_ID, screenContactData.getContactId());

            //Initials TextView
            TextView contactViewInitialsTemplate = (TextView) frameView.findViewById(R.id.clientContactTextView);

            TextView initialsTextView = new TextView(context);
            initialsTextView.setLayoutParams(contactViewInitialsTemplate.getLayoutParams());
            initialsTextView.setPadding(contactViewInitialsTemplate.getPaddingLeft(), contactViewInitialsTemplate.getPaddingTop(), contactViewInitialsTemplate.getPaddingRight(), contactViewInitialsTemplate.getPaddingBottom());
            initialsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, contactViewInitialsTemplate.getTextSize() / 2);
            initialsTextView.setGravity(contactViewInitialsTemplate.getGravity());
            String fullname = screenContactData.getFirstName() != null ? screenContactData.getFirstName() + Constants.CHAR.SPACE + screenContactData.getLastName() : screenContactData.getFirstName();
            initialsTextView.setText(Utils.getInitials(fullname));

            if (screenContactData.getGender().equals(Constants.GENDER.FEMALE)) {
                initialsTextView.setBackground(context.getResources().getDrawable(R.color.female_light_pink));
                initialsTextView.setTextColor(context.getResources().getColor(R.color.female_pink));
            } else if (screenContactData.getGender().equals(Constants.GENDER.MALE)) {
                initialsTextView.setBackground(context.getResources().getDrawable(R.color.male_light_blue));
                initialsTextView.setTextColor(context.getResources().getColor(R.color.male_blue));
            } else if (screenContactData.getGender().equals(Constants.GENDER.TRANSGENDER)) {
                initialsTextView.setBackground(context.getResources().getDrawable(R.color.gender_neutral_light_green));
                initialsTextView.setTextColor(context.getResources().getColor(R.color.gender_neutral_green));
            }

            ImageView indicatorImageViewTemplate = (ImageView) frameView.findViewById(R.id.clientContactIndicatorImageView);
            ImageView indicatorImageView = new ImageView(context);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicatorImageViewTemplate.getLayoutParams();
            layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
            indicatorImageView.setLayoutParams(layoutParams);
            indicatorImageView.setPadding(indicatorImageViewTemplate.getPaddingLeft(), indicatorImageViewTemplate.getPaddingTop(), indicatorImageViewTemplate.getPaddingRight(), indicatorImageViewTemplate.getPaddingBottom());
            indicatorImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            indicatorImageView.setId(View.generateViewId());
            indicatorImageView.setTag(R.id.CONTACT, screenContactData);

            if (screenContactData.isNegative() != null && screenContactData.isNegative()) {
                indicatorImageView.setVisibility(View.GONE);
                initialsTextView.setBackground(context.getResources().getDrawable(R.color.disabled_light_gray));
                initialsTextView.setTextColor(context.getResources().getColor(R.color.disabled_gray));
            } else {
                if (screenContactData.getStage().equals(Constants.SCREEN_STAGE.SCREENED)) {
                    indicatorImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_indicator_screened));
                } else if (screenContactData.getStage().equals(Constants.SCREEN_STAGE.DIAGNOSED)) {
                    indicatorImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_indicator_diagnosed));
                } else if (screenContactData.getStage().equals(Constants.SCREEN_STAGE.INTREATMENT)) {
                    indicatorImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_indicator_intreatment));
                }
            }

            //Add them up
            frameLayout.addView(initialsTextView);
            frameLayout.addView(indicatorImageView);
        } else {
            Log.e(TAG, "No FrameLayout found with identifier clientContactFrameLayout Found");
        }

    }

    public FrameLayout getScreenContactView() {
        return this.frameLayout;
    }
}
