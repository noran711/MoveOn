package com.examplehjhk.moveon;

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
import android.widget.Space;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.examplehjhk.moveon.domain.Group;
import com.examplehjhk.moveon.domain.User;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity responsible for managing and displaying social therapy groups.
 * Users can view groups, add new members, favorite members, or create new groups.
 */
public class GroupsActivity extends AppCompatActivity {

    private LinearLayout groupsContainer;
    // In-memory list to store groups during the activity lifecycle
    private final List<Group> groups = new ArrayList<>();
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_screen);

        // Retrieve the logged-in user session
        currentUser = (User) getIntent().getSerializableExtra("user");
        groupsContainer = findViewById(R.id.groupsContainer);

        // Setup UI components and navigation
        setupTopBar();
        setupBottomNavigation();

        // Populate with dummy data for demonstration if list is empty
        seedDefaultGroupsIfEmpty();

        // Render the list of groups dynamically
        renderGroups();
    }

    // Top bar and add Group

    /**
     * Initializes the top navigation bar and the global "Add Group" button.
     */
    private void setupTopBar() {
        ImageView btnHomeLogo = findViewById(R.id.btnHomeLogo);
        if (btnHomeLogo != null) {
            btnHomeLogo.setOnClickListener(v -> {
                // Return to home screen while passing the user session
                Intent intent = new Intent(GroupsActivity.this, MainActivity.class);
                intent.putExtra("user", currentUser);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        View btnAddNewGroup = findViewById(R.id.btnAddNewGroup);
        if (btnAddNewGroup != null) {
            btnAddNewGroup.setOnClickListener(v -> showAddGroupDialog());
        }
    }

    // bottom navigation

    /**
     * Sets up the bottom navigation menu and highlights the current "Groups" tab.
     */
    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navGroups = findViewById(R.id.navGroups);
        LinearLayout navSettings = findViewById(R.id.navSettings);

        // Visual feedback for the current tab
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

        // Current tab remains inactive
        if (navGroups != null) {
            navGroups.setOnClickListener(v -> { /* Already here */ });
        }
    }

    /**
     * Seeds the list with default groups if no groups are currently loaded.
     */
    private void seedDefaultGroupsIfEmpty() {
        if (!groups.isEmpty()) return;

        String owner = (currentUser != null) ? currentUser.username : "unknown";

        Group g1 = new Group("Survivors", owner);
        g1.addMember("Leonie Schwaiger");
        g1.addMember("Regina Enzner");

        Group g2 = new Group("Warriors", owner);
        g2.addMember("Max Mustermann");

        groups.add(g1);
        groups.add(g2);
    }


    /**
     * Dynamically clears and rebuilds the group list UI based on the domain data.
     */
    private void renderGroups() {
        if (groupsContainer == null) return;

        // Clear existing views to prevent duplication on refresh
        groupsContainer.removeAllViews();

        float density = getResources().getDisplayMetrics().density;

        // Iterate through domain objects and create UI components
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            int groupNumber = i + 1;

            CardView card = buildGroupCard(groupNumber, group, density);
            groupsContainer.addView(card);

            // Add spacing between cards
            Space space = new Space(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    (int) (12 * density)
            ));
            groupsContainer.addView(space);
        }

        // Re-add the "Add new Group" button at the bottom of the list
        addAddGroupButton(density);
    }

    /**
     * Creates and adds the Material Design button for creating new groups.
     */
    private void addAddGroupButton(float density) {
        MaterialButton btnAdd = new MaterialButton(this);
        btnAdd.setText("Add new Group");
        btnAdd.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#048CFA")));
        btnAdd.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                (int) (198 * density),
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        p.topMargin = (int) (8 * density);
        p.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        btnAdd.setLayoutParams(p);

        btnAdd.setOnClickListener(v -> showAddGroupDialog());

        groupsContainer.addView(btnAdd);
    }

    /**
     * Builds a CardView containing group information and member lists.
     */
    private CardView buildGroupCard(int groupNumber, Group group, float density) {
        CardView card = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, (int) (12 * density));
        card.setLayoutParams(cardParams);
        card.setRadius(8 * density);
        card.setCardElevation(2 * density);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int padding = (int) (12 * density);
        layout.setPadding(padding, padding, padding, padding);

        // Group Title Header
        TextView title = new TextView(this);
        title.setText("Group " + groupNumber + ": " + group.getGroupName());
        title.setTextSize(18);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#070000"));

        Space titleSpace = new Space(this);
        titleSpace.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (12 * density)
        ));

        // Container for member rows
        TableLayout table = new TableLayout(this);
        table.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));

        // Render each member from the domain object
        for (String memberName : group.getMembers()) {
            table.addView(buildMemberRow(group, memberName));
        }

        // Setup the "Add Member" input area
        LinearLayout addMemberLayout = new LinearLayout(this);
        addMemberLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams addMemberParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
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
        btnAdd.setCornerRadius((int) (24 * density));

        btnAdd.setOnClickListener(v -> {
            String name = input.getText() == null ? "" : input.getText().toString().trim();
            if (name.isEmpty()) return;

            // Update domain model
            group.addMember(name);

            // Refresh UI to show the new member
            renderGroups();
        });

        addMemberLayout.addView(input);
        addMemberLayout.addView(btnAdd);

        layout.addView(title);
        layout.addView(titleSpace);
        layout.addView(table);
        layout.addView(addMemberLayout);

        card.addView(layout);
        return card;
    }

    /**
     * Builds a single row within the member table with interaction buttons.
     */
    private TableRow buildMemberRow(Group group, String memberName) {
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView tvName = new TextView(this);
        tvName.setText(memberName);
        tvName.setTextColor(Color.BLACK);
        tvName.setTextSize(14);

        Space space = new Space(this);
        TableRow.LayoutParams spaceParams =
                new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        space.setLayoutParams(spaceParams);

        ImageButton btnFav = new ImageButton(this);
        btnFav.setImageResource(R.drawable.outline_favorite_24);
        btnFav.setBackgroundColor(Color.WHITE);
        btnFav.setPadding(0, 0, 0, 0);

        ImageButton btnDel = new ImageButton(this);
        btnDel.setImageResource(R.drawable.delete_outline_24);
        btnDel.setBackgroundColor(Color.WHITE);
        btnDel.setPadding(0, 0, 0, 0);

        // Toggle Favorite: pure visual change for this demonstration
        btnFav.setOnClickListener(v -> {
            if (tvName.getTypeface() != null && tvName.getTypeface().isBold()) {
                tvName.setTypeface(null, Typeface.NORMAL);
                btnFav.setColorFilter(null);
            } else {
                tvName.setTypeface(null, Typeface.BOLD);
                btnFav.setColorFilter(Color.RED);
            }
        });

        // Delete: Update domain and trigger UI refresh
        btnDel.setOnClickListener(v -> {
            group.removeMember(memberName);
            renderGroups();
        });

        row.addView(tvName);
        row.addView(space);
        row.addView(btnFav);
        row.addView(btnDel);

        return row;
    }

    // add group dialog

    /**
     * Displays an AlertDialog to capture a name for a new group.
     */
    private void showAddGroupDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Group");

        final EditText input = new EditText(this);
        input.setHint("Enter group name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String groupName = input.getText() == null ? "" : input.getText().toString().trim();
            if (groupName.isEmpty()) return;

            // Create and add new group to the list
            String owner = (currentUser != null) ? currentUser.username : "unknown";
            groups.add(new Group(groupName, owner));

            // Refresh the entire container
            renderGroups();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}