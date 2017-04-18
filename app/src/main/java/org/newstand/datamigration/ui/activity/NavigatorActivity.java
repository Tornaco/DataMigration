package org.newstand.datamigration.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.ActionListener2Adapter;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.repo.BKSessionRepoService;
import org.newstand.datamigration.repo.ReceivedSessionRepoService;
import org.newstand.datamigration.secure.VersionCheckResult;
import org.newstand.datamigration.secure.VersionInfo;
import org.newstand.datamigration.secure.VersionRetriever;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.ui.widget.ErrDialog;
import org.newstand.datamigration.ui.widget.IntroDialog;
import org.newstand.datamigration.ui.widget.VersionInfoDialog;
import org.newstand.datamigration.utils.Files;
import org.newstand.datamigration.worker.transport.Session;

import java.util.Date;

import cn.iwgang.simplifyspan.SimplifySpanBuild;
import cn.iwgang.simplifyspan.unit.SpecialTextUnit;
import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.shape.ShapeType;
import co.mobiwise.materialintro.view.MaterialIntroView;
import io.reactivex.functions.Consumer;
import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;

public class NavigatorActivity extends TransitionSafeActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigator);

        setTitle(getTitle());

        findView(R.id.card_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCard1Pop(findView(v, android.R.id.text2));
            }
        });

        findViewById(R.id.card_2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCard2Pop(findView(v, android.R.id.text2));
            }
        });

        findView(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionTo(new Intent(NavigatorActivity.this, HelpActivity.class));
            }
        });

        // Now hide the cards first.
        hideCards();

        if (SettingsProvider.shouldCheckForUpdateNow()) {
            checkForUpdate();
        }
    }

    private void hideCards() {
        findView(R.id.card_1).setVisibility(View.INVISIBLE);
        findView(R.id.card_2).setVisibility(View.INVISIBLE);
    }

    private void showCards() {
        findView(R.id.card_1).setVisibility(View.VISIBLE);
        findView(R.id.card_2).setVisibility(View.VISIBLE);
    }

    private void buildSummaryForCard1() {
        TextView introView = (TextView) findView(R.id.card_1).findViewById(android.R.id.text1);
        SimplifySpanBuild spanBuild = new SimplifySpanBuild(getString(R.string.backup_intro_actions));
        spanBuild.append(new SpecialTextUnit(getString(R.string.title_backup))
                .useTextBold()
                .setTextColor(ContextCompat.getColor(NavigatorActivity.this, R.color.accent)))
                .append(getString(R.string.backup_intro_and))
                .append(new SpecialTextUnit(getString(R.string.title_restore))
                        .useTextBold()
                        .setTextColor(ContextCompat.getColor(NavigatorActivity.this, R.color.accent)))
                .append("\n")
                .append(getString(R.string.backup_intro));
        introView.setText(spanBuild.build());
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStart() {
        super.onStart();

        // Show intro dialog
        IntroDialog.attach(NavigatorActivity.this, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        }, new Runnable() {
            @Override
            public void run() {
                // Ask for perms
                requestPerms();
            }
        });


    }

    private void requestPerms() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) throws Exception {
                        if (granted) {
                            onPermissionGrant();
                        } else {
                            onPermissionNotGrant();
                        }
                    }
                });
    }


    private void checkForUpdate() {
        VersionRetriever.hasLaterVersionAsync(this, new ActionListener2Adapter<VersionCheckResult, Throwable>() {
            @Override
            public void onComplete(VersionCheckResult versionCheckResult) {
                super.onComplete(versionCheckResult);
                if (versionCheckResult.isHasLater()) {
                    showUpdateSnake(versionCheckResult.getVersionInfo());
                    SettingsProvider.setLastUpdateCheckTime(System.currentTimeMillis());
                }
            }
        });
    }

    private void showUpdateSnake(final VersionInfo info) {
        if (isDestroyedCompat()) return;
        Snackbar.make(findView(R.id.fab),
                getString(R.string.title_new_update_available, info.getVersionName()),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.action_look_up, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRequestLookup(info);
                    }
                }).show();
    }

    private void onRequestLookup(VersionInfo info) {
        VersionInfoDialog.attach(NavigatorActivity.this, info);
    }

    private void onPermissionGrant() {
        showCards();
        // Show card intro after our cards is visible~
        buildCardIntros();
        queryShowHistory();
    }

    private void buildCardIntros() {
        // Show card intros.
        new MaterialIntroView.Builder(this)
                .enableDotAnimation(true)
                .enableIcon(true)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(getString(R.string.card_intro))
                .setShape(ShapeType.CIRCLE)
                .setTarget(findView(R.id.card_1))
                // Always show when in dev mode.
                .setUsageId("intro_card_1")
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String s) {
                        onCardsIntroClick();
                    }
                })
                .show();
    }

    private void onCardsIntroClick() {
    }

    private void onPermissionNotGrant() {
        ErrDialog.attach(NavigatorActivity.this, new IllegalStateException("Permission denied"), new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finishWithAfterTransition();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void queryShowHistory() {
        final TextView tv1 = findView(findView(R.id.card_1), android.R.id.text2);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Session last = BKSessionRepoService.get().findLast();
                final String intro;
                if (last == null) {
                    intro = getString(R.string.title_backup_history_noop);
                } else {
                    intro = getString(R.string.title_backup_history,
                            FuzzyDateTimeFormatter.getTimeAgo(getApplicationContext(),
                                    new Date(last.getDate())));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv1.setText(intro);
                    }
                });
            }
        });

        final TextView tv2 = findView(findView(R.id.card_2), android.R.id.text2);
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int size = ReceivedSessionRepoService.get().size();
                final String intro;
                if (size == 0) {
                    intro = getString(R.string.title_receive_history_noop);
                } else {
                    intro = getString(R.string.title_receive_history_size, String.valueOf(size));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv2.setText(intro);
                    }
                });
            }
        });
    }

    private void showCard1Pop(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backup:
                        onActionBackup();
                        break;
                    case R.id.action_restore:
                        onActionRestore();
                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.navigator_card_1);
        popup.show();
    }

    private void onActionBackup() {
        transitionTo(new Intent(NavigatorActivity.this, AndroidCategoryViewerActivity.class));
    }

    private void onActionRestore() {
        transitionTo(new Intent(NavigatorActivity.this, BackupSessionPickerActivity.class));
    }

    private void showCard2Pop(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_send:
                        transitionTo(new Intent(NavigatorActivity.this, WFDDataSenderActivity.class));
                        break;
                    case R.id.action_receive:
                        transitionTo(new Intent(NavigatorActivity.this, WFDDataReceiverActivity.class));
                        break;
                    case R.id.action_received_viewer:
                        transitionTo(new Intent(NavigatorActivity.this, ReceivedSessionPickerActivity.class));
                        break;
                    case R.id.action_share:
                        onRequestShare();
                        break;
                }
                return true;
            }
        });
        popup.inflate(R.menu.navigator_card_2);
        popup.show();
    }

    private void onRequestShare() {
        if (!SettingsProvider.isTipsNoticed("onRequestShare"))
            new AlertDialog.Builder(NavigatorActivity.this)
                    .setTitle(R.string.title_transport_share)
                    .setMessage(R.string.title_transport_share_tips)
                    .setCancelable(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            org.newstand.datamigration.utils.Files.shareDateMigrationAsync(NavigatorActivity.this);
                        }
                    })
                    .setNeutralButton(R.string.title_never_remind, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SettingsProvider.setTipsNoticed("onRequestShare", true);
                            org.newstand.datamigration.utils.Files.shareDateMigrationAsync(NavigatorActivity.this);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
                    .show();
        else Files.shareDateMigrationAsync(NavigatorActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_user_actions).setVisible(SettingsProvider.isDebugEnabled());
        menu.findItem(R.id.action_scheduler).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            transitionTo(new Intent(this, SettingsActivity.class));
        }

        if (id == R.id.action_user_actions) {
            transitionTo(new Intent(this, UserActionViewerActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected boolean isMainActivity() {
        return true;
    }
}
