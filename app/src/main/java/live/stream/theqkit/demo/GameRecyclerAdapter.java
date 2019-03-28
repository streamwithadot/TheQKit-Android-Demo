package live.stream.theqkit.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import live.stream.theq.theqkit.data.sdk.GameResponse;
import live.stream.theq.theqkit.util.CurrencyHelper;

public class GameRecyclerAdapter extends RecyclerView.Adapter<GameRecyclerAdapter.GameViewHolder> {

  private Context context;
  private GameClickedListener listener;

  public GameRecyclerAdapter(@NonNull Context context, @NonNull GameClickedListener listener) {
    this.context = context;
    this.listener = listener;
  }

  private List<GameResponse> games = new ArrayList<>();

  class GameViewHolder extends RecyclerView.ViewHolder {
    private TextView type;
    private TextView payout;
    private TextView scheduledTime;
    private Button playGame;
    private DateFormat dateFormat;
    private GameViewHolder(View v) {
      super(v);
      type = v.findViewById(R.id.gameType);
      payout = v.findViewById(R.id.gamePayout);
      scheduledTime = v.findViewById(R.id.scheduledTime);
      playGame = v.findViewById(R.id.playButton);
      dateFormat = SimpleDateFormat.getDateTimeInstance();
    }

    private void bind(final GameResponse game) {
      type.setText(game.getGameType());
      payout.setText(CurrencyHelper.INSTANCE.getRoundedCurrency(context, game.getReward()));
      scheduledTime.setText(dateFormat.format(new Date(game.getScheduled())));

      if (game.getActive()) {
        scheduledTime.setVisibility(View.GONE);
        playGame.setVisibility(View.VISIBLE);
        playGame.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View v) {
            if (listener != null) {
              listener.onGameClicked(game);
            }
          }
        });
      } else {
        playGame.setVisibility(View.GONE);
        scheduledTime.setVisibility(View.VISIBLE);
      }
    }
  }

  @Override
  public @NonNull GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.row_game, parent, false);

    return new GameViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
    holder.bind(games.get(position));
  }

  @Override
  public int getItemCount() {
    return games.size();
  }

  public void setGames(@NonNull List<GameResponse> games) {
    this.games = games;
    notifyDataSetChanged();
  }

  interface GameClickedListener {
    void onGameClicked(GameResponse game);
  }
}