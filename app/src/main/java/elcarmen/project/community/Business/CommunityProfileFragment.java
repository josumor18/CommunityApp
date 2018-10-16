package elcarmen.project.community.Business;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.concurrent.ExecutionException;

import elcarmen.project.community.Data.HttpGetBitmap;
import elcarmen.project.community.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityProfileFragment extends Fragment {


    public CommunityProfileFragment() {
        // Required empty public constructor
    }

    EditText edtName, edtDescription, edtRule;
    ImageView imgCom;
    Button btnAddRule, btnUpImage;

    int idCommu;

    User_Singleton user;
    boolean isAdmin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_community_profile, container, false);

        edtName = v.findViewById(R.id.edtxtCommNameProfile);edtDescription = v.findViewById(R.id.edtxtCommDescriptionProfile);
        edtRule = v.findViewById(R.id.edtxtReglaConvivProfile);
        imgCom = v.findViewById(R.id.imgComImageProfile);

        btnAddRule = v.findViewById(R.id.btnAddRuleProfile); btnUpImage = v.findViewById(R.id.btnUploadComImageProfile);

        user = User_Singleton.getInstance();

        String photo = "";

        for(Community e: (CommunitiesFragment.communities)){
            if (e.getId() == CommunityActivity.idCommunity){
                photo = e.getUrl_photo();
                edtDescription.setText(e.getDescription());


            }
        }

        HttpGetBitmap request = new HttpGetBitmap();
        Bitmap commImage = null;
        try {
            commImage = request.execute(photo).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (commImage == null) {
            commImage = BitmapFactory.decodeResource(getContext().getResources(),
                    R.drawable.img_comm_default);
        }

        imgCom.setImageBitmap(commImage);
        edtName.setText(CommunityActivity.nameCommunity);

        isAdmin = user.isAdmin(CommunityActivity.idCommunity);
        if(!isAdmin){
            disableEditText(edtName);
            disableEditText(edtDescription);
            edtRule.setVisibility(View.GONE);
            btnUpImage.setVisibility(View.GONE);
            btnAddRule.setVisibility(View.GONE);

        }
        return v;
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        
    }





}
