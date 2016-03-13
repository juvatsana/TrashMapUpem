package fr.upem.trashmapupem;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by Mourougan on 06/03/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PbViewHolder>{

    List<FragmentListDistance.Data> datas;
    FragmentActivity context;
    static List<PoubelleMarker> listP;

    RVAdapter(List<FragmentListDistance.Data> datas, FragmentActivity activity, List<PoubelleMarker> thelist){
        this.datas = datas;
        this.context = activity;
        listP = thelist;
    }

    /***
     * Overwrited method all all clickable viewHolder in Adapter. Implements interface ViewHolderClick to change on click method.
     * Call
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public PbViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RVAdapter.PbViewHolder.ViewHolderClick vhc = new PbViewHolder.ViewHolderClick() {
            @Override
            public void onclickdist(View caller, CharSequence text, int position) {
                MainActivity main = (MainActivity)context;
                main.loadFragmentMapTrack(FragmentMap.FM_CONFIG.TRACK,listP.get(position));

            }
        };
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        PbViewHolder pbh = new PbViewHolder(v,context,vhc);
        return pbh;
    }


    @Override
    public void onBindViewHolder(PbViewHolder holder, int position) {
        holder.type.setText(datas.get(position).type.toString());

        double distance = datas.get(position).distance;
        double partieEntiere = Math.floor(distance);
        double partieDecimale = Math.floor((distance - partieEntiere) * 1000);
        String distanceFormated = Double.toString(partieEntiere) + "km "+ Double.toString(partieDecimale)+ " m";

        holder.distance.setText(distanceFormated);
        holder.photoPb.setImageResource(datas.get(position).photoPb);

//
//        holder.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg) {
//                // TODO Auto-generated method stub
//                Intent browserIntent = new Intent(context, Uri.parse(animal.wikipedia_url));
//                startActivity(new Intent(this, MyFragmentActivity.class));
//                context.startActivity(browserIntent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class PbViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final FragmentActivity context;
        CardView cardView;
        TextView type;
        TextView distance;
        ImageView photoPb;
        public ViewHolderClick mListener;

        PbViewHolder(View itemView, FragmentActivity context,ViewHolderClick listener) {
            super(itemView);
            mListener = listener;
            cardView = (CardView)itemView.findViewById(R.id.card);

            type = (TextView)itemView.findViewById(R.id.type_pb);
            distance = (TextView)itemView.findViewById(R.id.dist_pb);
            photoPb = (ImageView)itemView.findViewById(R.id.img_card_pb);

            cardView.setOnClickListener(this);

            this.context = context;
        }

        @Override
        public void onClick(View v) {

            mListener.onclickdist(v, distance.getText(),getPosition());
        }

        public static interface ViewHolderClick {
            public void onclickdist(View caller, CharSequence text, int position);

        }
    }

}