package org.fossasia.openevent.adapters.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.fossasia.openevent.R;
import org.fossasia.openevent.activities.SpeakerDetailsActivity;
import org.fossasia.openevent.data.Speaker;
import org.fossasia.openevent.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpeakerViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.speakers_list_image)
    protected ImageView speakerImage;

    @BindView(R.id.speakers_list_name)
    protected TextView speakerName;

    @BindView(R.id.speakers_list_designation)
    protected TextView speakerDesignation;

    @BindView(R.id.speakers_list_country)
    protected TextView speakerCountry;

    private Speaker speaker;
    private Context context;

    public SpeakerViewHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        this.context = context.getApplicationContext();

        //Attach onClickListener for ViewHolder
        itemView.setOnClickListener(view -> {
            String speakerName = speaker.getName();
            Intent intent = new Intent(this.context, SpeakerDetailsActivity.class);
            intent.putExtra(Speaker.SPEAKER, speakerName);
            this.context.startActivity(intent);
        });
    }

    public void bindSpeaker(Speaker speaker) {
        this.speaker = speaker;

        String thumbnail = Utils.parseImageUri(this.speaker.getThumbnail());
        if (thumbnail == null)
            thumbnail = Utils.parseImageUri(this.speaker.getPhoto());

        Drawable placeholder = VectorDrawableCompat.create(context.getResources(), R.drawable.ic_account_circle_grey_24dp, null);

        if(thumbnail != null) {
            Picasso.with(speakerImage.getContext())
                    .load(Uri.parse(thumbnail))
                    .placeholder(placeholder)
                    .into(speakerImage);
        } else {
            speakerImage.setImageDrawable(placeholder);
        }

        String name = this.speaker.getName();
        name = TextUtils.isEmpty(name) ? "" : name;

        String positionString = this.speaker.getPosition();
        positionString = TextUtils.isEmpty(positionString) ? "" : positionString;

        String country = this.speaker.getCountry();
        country = TextUtils.isEmpty(country) ? "" : country;

        speakerName.setText(name);
        speakerDesignation.setText(String.format(positionString, this.speaker.getOrganisation()));
        speakerCountry.setText(country);
    }
}
