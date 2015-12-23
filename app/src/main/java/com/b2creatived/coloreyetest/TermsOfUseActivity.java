package com.b2creatived.coloreyetest;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class TermsOfUseActivity extends AppCompatActivity {

    TextView mainTitle, tos_title1, tos_title2, tos_title3, tos_title4, tos_title5, tos_title6;
    TextView tos_text1, tos_text2, tos_text3, tos_text4, tos_text5, tos_text6;
    Typeface RobotoLight, RobotoMedium, RobotoRegular;
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.termsofuse);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();

        RobotoLight = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Light.otf");
        RobotoMedium = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Medium.otf");
        RobotoRegular = Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.otf");

        mainTitle = (TextView) findViewById(R.id.guidelines_maintitle);
        tos_title1 = (TextView) findViewById(R.id.tos_title1);
        tos_title2 = (TextView) findViewById(R.id.tos_title2);
        tos_title3 = (TextView) findViewById(R.id.tos_title3);
        tos_title4 = (TextView) findViewById(R.id.tos_title4);
        tos_title5 = (TextView) findViewById(R.id.tos_title5);
        tos_title6 = (TextView) findViewById(R.id.tos_title6);

        tos_text1 = (TextView) findViewById(R.id.tos_text1);
        tos_text2 = (TextView) findViewById(R.id.tos_text2);
        tos_text3 = (TextView) findViewById(R.id.tos_text3);
        tos_text4 = (TextView) findViewById(R.id.tos_text4);
        tos_text5 = (TextView) findViewById(R.id.tos_text5);
        tos_text6 = (TextView) findViewById(R.id.tos_text6);

        mainTitle.setText("Terms of Use");
        mainTitle.setTypeface(RobotoMedium);

        tos_title1.setText("1. DEFINITIONS AND INTERPRETATION");


        tos_text1.setText("Please read our Terms of Use (Services) before using our Services. By using Eye Test you are accepting the practices described in this Agreement.\n\n" +
                "1.1 In this Agreement, the following expressions have the following meanings:\n\n" +
                "By using Eye Test you agree to comply with and be bound by the following terms and conditions of use, which govern Eye Test's relationship with you and the provided services.\n\n"+

                "We will not sell, distribute or lease your personal information to third-parties unless we are required by law to do so.\n\n"+
                "Agreement: the agreement between the User and Eye Test\n" +
                "Application: the Eye Test app downloaded from the Google Play StoreTM\n" +
                "Content: any material, including, but not limited to images and text, submitted, sent, posted or uploaded in any way to the App\n" +
                "Member: registered user of Application\n\n" +

                "1.2 By accepting this Terms of the Agreement, the Member\n\n" +
                "(a) agrees to provide accurate, current and complete information about User\n" +
                "(b) agrees to maintain and update this information to keep it accurate, current and complete.\n");

        tos_title2.setText("2. TERMINATION OF SERVICE");

        tos_text2.setText("2.1 Eye Test may terminate the Service with or without cause at any time, effective immediately and without prior notice. Eye Test may terminate a Member via written or email notice as necessary. Should Member object to any Terms of the Agreement or any subsequent modifications hereto, or become dissatisfied with the Service in any way, the Member's sole recourse is to immediately contact Eye Test support by emailing directly to hello@Eye Test.net. Upon termination of the Service, Member's right to use the Service instantly ceases. Member shall have no right, and Eye Test shall have no obligation thereafter, to forward any information associated with Member's account.\n\n" +
                "2.2 Eye Test may terminate Member without any prior notice if Member has materially breached any provision of the Agreement. Eye Test reserves the right to immediately suspend or terminate Member and/or restrict Member's access to the Service until any breach or noncompliance is cured.\n\n" +
                "2.3 Eye Test may, but has no duty to, immediately terminate Member and remove it from the Service servers if in its sole discretion Eye Test concludes that Member is engaged in illegal activities or the sale of illegal or harmful goods or services, or is engaged in activities or sales that may damage the rights of Eye Test or others. Any termination under this Section shall take effect immediately, and Member expressly agrees that it shall not have any opportunity to cure.\n\n"+
                "2.4 Upon termination, Eye Test reserves the right to delete from its servers any and all information contained in Member's account including, but not limited to, personal data and order processing information.\n");

        tos_title3.setText("3. LIMITATION OF LIABILITY");
        tos_text3.setText("3.1 All Content posted or otherwise submitted to the App is the sole responsibility of the account holder from which such Content originates and you acknowledge and agree that you, and not Eye Test are entirely responsible for all Content that you post, or otherwise submit to the App. Eye Test does not control user submitted Content and, as such, does not guarantee the accuracy, integrity or quality of such Content. You understand that by using the App you may be exposed to Content that is offensive, indecent or objectionable.\n\n"+
                "3.2 As a condition of use, you promise not to use the App for any purpose that is unlawful or prohibited by these Terms, or any other purpose not reasonably intended by Eye Test. You agree not to use the App:\n\n"+
                "(a) To abuse, harass, threaten, impersonate or intimidate any person;\n"+
                "(b) To post or transmit, or cause to be posted or transmitted, any Content that is libellous, defamatory, obscene, pornographic, abusive, offensive, profane, or that infringes any copyright or other right of any person;\n"+
                "(c) To post or transmit, or cause to be posted or transmitted, any communication or solicitation designed or intended to obtain password, account, or private information from any Eye Test user;\n"+
                "(d) To create or transmit unwanted spam to any person or any URL;\n"+
                "(e) To create multiple accounts for the purpose of voting for or against users' photographs or images;\n"+
                "(f) To post copyrighted Content which doesn't belong to you, with exception of Blogs, where you may post such Content with explicit mention of the author's name and a link to the source of the Content;\n"+
                "(g) To promote or sell Content of another person;\n"+
                "(h) To sell or otherwise transfer your profile.\n\n"+

                "3.3 You expressly understand and agree that Eye Test and their creators shall not be liable to you for:\n\n"+
                "(1) Any direct, indirect, incidental, special consequential or exemplary damages which may be incurred by you, however caused and under any theory of liability. This shall include, but not be limited to, any loss of profit (whether incurred directly or indirectly), any loss of goodwill or business reputation, any loss of data suffered, cost of procurement of substitute goods or Services, or other intangible loss;\n\n"+
                "(2) Any loss or damage which may be incurred by you, including but not limited to loss or damage as a result of:\n\n"+
                "(a) Any reliance placed by you on the completeness, accuracy or existence of any Order, or as a result of any relationship or transaction between you and any Customer or sponsor whose Order appears through the service;\n"+
                "(b) Any changes which Eye Test may make to the service, or for any permanent or temporary cessation in the provision of the service (or any features within the service);\n"+
                "(c) The deletion of, corruption of, or failure to store, any content and other communications data maintained or transmitted by or through your use of the service.\n"+
                "(d) Your failure to provide Eye Test with accurate account information;\n"+
                "(e) Your failure to keep your password or account details secure and confidential;\n"+
                "(f) Any damage that results from the service being temporarily unavailable due to technical issues beyond our control;\n\n"+
                "3.4 You are solely responsible for any damage to your mobile phone/operating system or other device or loss of data that results from using the Application.\n\n" +
                "3.5 Eye Test is not responsible for any personal or sensitive information you share about yourself or others through Eye Test's services.\n\n"+
                "3.6 You expressly understand that based on the data you provide about yourself in the Eye Test app you can be reached by third parties and their partners for marketing or other services.\n\n"+
                "3.7 Users may post links that point to World Wide Web sites or resources. The Eye Test has no control over such sites and resources, you acknowledge and agree that The Eye Test not responsible for the availability and accuracy of such sites or resources and does not endorse and is not responsible or liable for any content, advertising, products or other materials on or available from such sites or resources. You further acknowledge that Eye Test shall not be responsible or liable, directly or indirectly, for any damage or loss caused or alleged to be caused by or in connection with use of our reliance on any such content, goods or services available on or through any such site or resource.");


        tos_title4.setText("4. COPYRIGHT AND TRADEMARK POLICIES");
        tos_text4.setText("4.1 Eye Test respects the intellectual property rights of others. It is our policy to respond promptly any claim that Content posted in the App infringes the copyright or other intellectual prpoerty of any person.\n\n"+
                "4.2 To notify Eye Test of a possible infringement you must submit your notice in writing to hello@Eye Test.net and include in your notice a detailed description of the alleged infringement sufficient to enable Eye Test to make a reasonable determination. Please note that you may be held accountable for damages (including costs and attorneys' fees) for misrepresenting that any Content is infringing your copyright.");

        tos_title5.setText("5. CHANGES TO THE TERMS");
        tos_text5.setText("5.1 Eye Test reserves the right to make changes to the Service at any time.\n\n"+
                "5.2 You understand and agree that if you use the Service after the date on which the Agreement has changed, Eye Test will treat your use as acceptance of the updated Agreement.\n");

        tos_title6.setText("6. GENERAL LEGAL TERMS");
        tos_text6.setText("6.1 The Agreement constitutes the whole legal agreement between you and Eye Test and governs your use of the Service (however excluding any Services which Eye Test may provide to you under a separate written agreement), and completely replace any prior agreements between you and Eye Test in relation to the Service.\n\n"+
                "6.2 You agree that Eye Test may provide you with notices, including those regarding changes to the Agreement, by email, regular mail, or postings on the Service.\n\n"+
                "6.3 You agree that if Eye Test does not exercise or enforce any legal right or remedy which is contained in the Agreement (or which Eye Test has the benefit of under any applicable law), this will not be taken to be a formal waiver of Eye Test's rights and that those rights or remedies will still be available to Eye Test.\n\n"+
                "6.4 If any court of law, having the jurisdiction to decide on this matter, rules that any provision of the Agreement is invalid, then that provision will be removed from the Agreement without affecting the rest of the Terms. The remaining provisions of the Agreement will continue to be valid and enforceable.\n\n"+
                "6.5 The Agreement, and your relationship with Eye Test under the Terms, shall be governed by the laws of Hungary without regard to its conflict of laws provisions. You and Eye Test agree to submit to the exclusive jurisdiction of the courts located within the county of Budapest, Hungary to resolve any legal matter arising from the Agreement. Notwithstanding this, you agree that Eye Test shall still be allowed to apply for injunctive remedies (or an equivalent type of urgent legal relief) in any jurisdiction.");


        tos_text1.setTypeface(RobotoLight);
        tos_text2.setTypeface(RobotoLight);
        tos_text3.setTypeface(RobotoLight);
        tos_text4.setTypeface(RobotoLight);
        tos_text5.setTypeface(RobotoLight);
        tos_text6.setTypeface(RobotoLight);

        tos_title1.setTypeface(RobotoMedium);
        tos_title2.setTypeface(RobotoMedium);
        tos_title3.setTypeface(RobotoMedium);
        tos_title4.setTypeface(RobotoMedium);
        tos_title5.setTypeface(RobotoMedium);
        tos_title6.setTypeface(RobotoMedium);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mTracker != null) {
            mTracker.setScreenName("TermsOfUse");
            mTracker.send(new HitBuilders.ScreenViewBuilder().build());
            mTracker.enableAdvertisingIdCollection(true);
        }
    }

}
