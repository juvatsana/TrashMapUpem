package fr.upem.trashmapupem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.List;


/**
 * Created by Mourougan on 06/03/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PbViewHolder>{

    List<FragmentListDistance.Data> datas;

    RVAdapter(List<FragmentListDistance.Data> datas){
        this.datas = datas;
    }

    @Override
    public PbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        PbViewHolder pbh = new PbViewHolder(v);
        return pbh;
    }

    @Override
    public void onBindViewHolder(PbViewHolder holder, int position) {
        holder.type.setText(datas.get(position).type.toString());
        holder.distance.setText(datas.get(position).distance);
        holder.photoPb.setImageResource(datas.get(position).photoPb);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class PbViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView type;
        TextView distance;
        ImageView photoPb;

        PbViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card);
            type = (TextView)itemView.findViewById(R.id.type_pb);
            distance = (TextView)itemView.findViewById(R.id.dist_pb);
            photoPb = (ImageView)itemView.findViewById(R.id.img_card_pb);
        }
    }

}