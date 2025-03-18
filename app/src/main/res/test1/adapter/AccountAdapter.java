package com.example.test1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.test1.R;
import com.example.test1.entity.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private List<Account> accountList;
    private Context context;
    private Consumer<Integer> onDeleteClick;

    public AccountAdapter(List<Account> accountList, Context context, Consumer<Integer> onDeleteClick) {
        this.accountList = accountList != null ? accountList : new ArrayList<>();
        this.context = context;
        this.onDeleteClick = onDeleteClick;
    }

    @Override
    public AccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        Account account = accountList.get(position);
        holder.textUsername.setText(account.getUsername());
        holder.textEmail.setText(account.getEmail());
        holder.textRole.setText(account.getRoleId() == 0 ? "Admin" : "User");

        holder.buttonDelete.setOnClickListener(v -> {
            if (onDeleteClick != null) {
                onDeleteClick.accept(account.getAccountId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public void updateList(List<Account> newList) {
        this.accountList = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView textUsername, textEmail, textRole;
        Button buttonDelete;

        public AccountViewHolder(View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            textRole = itemView.findViewById(R.id.textRole);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}