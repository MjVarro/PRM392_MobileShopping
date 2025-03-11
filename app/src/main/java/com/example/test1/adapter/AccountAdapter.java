package com.example.test1.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.dtb.DatabaseHelper;
import com.example.test1.entity.Account;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private List<Account> accountList;
    private Context context;
    private DatabaseHelper dbHelper;

    public AccountAdapter(List<Account> accountList, Context context) {
        this.accountList = accountList;
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.accountIdText.setText(String.valueOf(account.getAccountId()));
        holder.usernameText.setText(account.getUsername());

//        holder.viewButton.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ViewUserProfileActivity.class);
//            intent.putExtra("ACCOUNT_ID", account.getAccountId());
//            context.startActivity(intent);
//        });

        holder.deleteButton.setOnClickListener(v -> {
            dbHelper.deleteAccount(account.getAccountId());
            accountList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, accountList.size());
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView accountIdText, usernameText, viewButton, deleteButton;

        public AccountViewHolder(View itemView) {
            super(itemView);
            accountIdText = itemView.findViewById(R.id.tvAccountId);
            usernameText = itemView.findViewById(R.id.tvUsername);
            viewButton = itemView.findViewById(R.id.tvView);
            deleteButton = itemView.findViewById(R.id.tvDelete);
        }
    }
}
