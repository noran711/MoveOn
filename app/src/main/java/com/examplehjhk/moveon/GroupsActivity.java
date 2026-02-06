package com.examplehjhk.moveon;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Space;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.examplehjhk.moveon.domain.User;
import com.google.android.material.button.MaterialButton;

public class GroupsActivity extends AppCompatActivity {

    private LinearLayout groupsContainer;
    private int groupCount = 2;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_screen);

        currentUser = (User) getIntent().getSerializableExtra("user");
        groupsContainer = findViewById(R.id.groupsContainer);

        // Back button (Logo) logic - using the correct ID from layout
        ImageView btnHomeLogo = findViewById(R.id.btnHomeLogo);
        if (btnHomeLogo != null) {
            btnHomeLogo.setOnClickListener(v -> {
                Intent intent = new Intent(GroupsActivity.this, MainActivity.class);
                intent.putExtra("user", currentUser);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        // Setup existing members
        setupMemberActions(R.id.btnFavorite11, R.id.lblGroup1Member1, R.id.btnDelete11);
        setupMemberActions(R.id.btnFavorite12, R.id.lblGroup1Member2, R.id.btnDelete12);
        setupMemberActions(R.id.btnFavorite21, R.id.lblGroup2Member1, R.id.btnDelete21);

        // Add Member Buttons for existing groups
        View btnAddMember1 = findViewById(R.id.btnAddMember1);
        if (btnAddMember1 != null) {
            btnAddMember1.setOnClickListener(v -> 
                    addNewMember(findViewById(R.id.tableGroup1), findViewById(R.id.txtAddMember1)));
        }

        View btnAddMember2 = findViewById(R.id.btnAddMember2);
        if (btnAddMember2 != null) {
            btnAddMember2.setOnClickListener(v -> 
                    addNewMember(findViewById(R.id.tableGroup2), findViewById(R.id.txtAddMember2)));
        }

        // Add New Group Button
        View btnAddNewGroup = findViewById(R.id.btnAddNewGroup);
        if (btnAddNewGroup != null) {
            btnAddNewGroup.setOnClickListener(v -> showAddGroupDialog());
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navGroups = findViewById(R.id.navGroups);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        // Highlight Groups (Current Screen)
        ImageView iconGroups = findViewById(R.id.iconGroups);
        TextView textGroups = findViewById(R.id.textGroups);
        if (iconGroups != null) iconGroups.setColorFilter(Color.parseColor("#048CFA"));
        if (textGroups != null) textGroups.setTextColor(Color.parseColor("#048CFA"));

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                Intent intent = new Intent(GroupsActivity.this, MainActivity.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            });
        }

        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                Intent intent = new Intent(GroupsActivity.this, Settings.class);
                intent.putExtra("user", currentUser);
                startActivity(intent);
            });
        }
    }

    private void showAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Group");

        final EditText input = new EditText(this);
        input.setHint("Enter group name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String groupName = input.getText().toString().trim();
            if (!groupName.isEmpty()) {
                createNewGroup(groupName);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void addNewMember(TableLayout table, EditText input) {
        if (table == null || input == null) return;
        String name = input.getText().toString().trim();
        if (name.isEmpty()) return;

        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView tvName = new TextView(this);
        tvName.setText(name);
        tvName.setTextColor(Color.BLACK);
        tvName.setTextSize(14);

        Space space = new Space(this);
        TableRow.LayoutParams spaceParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        space.setLayoutParams(spaceParams);

        ImageButton btnFav = new ImageButton(this);
        btnFav.setImageResource(R.drawable.outline_favorite_24);
        btnFav.setBackgroundColor(Color.WHITE);
        btnFav.setPadding(0, 0, 0, 0);

        ImageButton btnDel = new ImageButton(this);
        btnDel.setImageResource(R.drawable.delete_outline_24);
        btnDel.setBackgroundColor(Color.WHITE);
        btnDel.setPadding(0, 0, 0, 0);

        row.addView(tvName);
        row.addView(space);
        row.addView(btnFav);
        row.addView(btnDel);

        table.addView(row);
        input.setText("");

        btnFav.setOnClickListener(v -> {
            if (tvName.getTypeface() != null && tvName.getTypeface().isBold()) {
                tvName.setTypeface(null, Typeface.NORMAL);
                btnFav.setColorFilter(null);
            } else {
                tvName.setTypeface(null, Typeface.BOLD);
                btnFav.setColorFilter(Color.RED);
            }
        });

        btnDel.setOnClickListener(v -> table.removeView(row));
    }

    private void createNewGroup(String groupName) {
        groupCount++;
        float density = getResources().getDisplayMetrics().density;
        
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, (int) (12 * density));
        card.setLayoutParams(cardParams);
        card.setRadius(8 * density);
        card.setCardElevation(2 * density);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (12 * density);
        layout.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Group " + groupCount + ": " + groupName);
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#070000"));

        Space titleSpace = new Space(this);
        titleSpace.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, (int) (12 * density)));

        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout addMemberLayout = new LinearLayout(this);
        addMemberLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams addMemberParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addMemberParams.setMargins(0, (int) (16 * density), 0, 0);
        addMemberLayout.setLayoutParams(addMemberParams);

        EditText input = new EditText(this);
        input.setHint("Add new member");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        input.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        MaterialButton btnAdd = new MaterialButton(this);
        btnAdd.setText("Add");
        btnAdd.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#048CFA")));
        btnAdd.setTextColor(Color.WHITE);
        btnAdd.setCornerRadius((int) (24 * density)); // Makes it rounded
        btnAdd.setOnClickListener(v -> addNewMember(table, input));

        addMemberLayout.addView(input);
        addMemberLayout.addView(btnAdd);

        layout.addView(title);
        layout.addView(titleSpace);
        layout.addView(table);
        layout.addView(addMemberLayout);
        card.addView(layout);

        groupsContainer.addView(card);
    }

    private void setupMemberActions(int favoriteBtnId, int nameLabelId, int deleteBtnId) {
        ImageButton btnFavorite = findViewById(favoriteBtnId);
        TextView lblName = findViewById(nameLabelId);
        ImageButton btnDelete = findViewById(deleteBtnId);

        if (btnFavorite != null && lblName != null) {
            btnFavorite.setOnClickListener(v -> {
                if (lblName.getTypeface() != null && lblName.getTypeface().isBold()) {
                    lblName.setTypeface(null, Typeface.NORMAL);
                    btnFavorite.setColorFilter(null);
                } else {
                    lblName.setTypeface(null, Typeface.BOLD);
                    btnFavorite.setColorFilter(Color.RED);
                }
            });
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                View parent = (View) v.getParent();
                if (parent instanceof TableRow) {
                    ((TableLayout)parent.getParent()).removeView(parent);
                }
            });
        }
    }
}
