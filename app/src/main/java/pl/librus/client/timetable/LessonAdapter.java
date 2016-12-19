package pl.librus.client.timetable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.librus.client.R;
import pl.librus.client.api.EventCategory;
import pl.librus.client.api.Lesson;
import pl.librus.client.api.LibrusData;
import pl.librus.client.api.SchoolDay;
import pl.librus.client.api.SchoolWeek;
import pl.librus.client.api.Subject;
import pl.librus.client.api.Teacher;

class LessonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 45;
    private static final int VIEW_TYPE_LESSON = 42;
    private final List<SchoolDay> schoolDays = new ArrayList<>();
    private final Map<String, Subject> subjectMap;
    private Map<String, EventCategory> eventCategoryMap;
    private Map<String, Teacher> teacherMap;
    private List<Object> listElements = new ArrayList<>();

    LessonAdapter(LibrusData data) {
        List<SchoolWeek> schoolWeeks = data.getSchoolWeeks();
        for (SchoolWeek schoolWeek : schoolWeeks) {
            schoolDays.addAll(schoolWeek.getSchoolDays());
        }
        this.eventCategoryMap = data.getEventCategoriesMap();
        this.teacherMap = data.getTeacherMap();
        this.subjectMap = data.getSubjectMap();
        Collections.sort(schoolDays);
        for (SchoolDay schoolDay : schoolDays) {
            String title;
            String subtitle = schoolDay.getDate().toString("d.M");
            if (schoolDay.getDate().equals(LocalDate.now())) {
                title = "Dzisiaj";
            } else if (schoolDay.getDate().equals(LocalDate.now().plusDays(1))) {
                title = "Jutro";
            } else {
                title = schoolDay.getDate().toString("EEEE", new Locale("pl"));
                subtitle = schoolDay.getDate().toString("d.M");
            }
            SpannableString sectionText = new SpannableString(title.substring(0, 1).toUpperCase() + title.substring(1) + ' ' + subtitle);
            sectionText.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            listElements.add(sectionText);
            for (int i = 0; i < schoolDay.getLastLesson(); i++) {
                Lesson lesson = schoolDay.getLesson(i);
                if (lesson != null) {
                    listElements.add(lesson);
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_LESSON) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.lesson_item, parent, false);
            return new LessonViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_subheader, parent, false);
            return new SubheaderViewHolder(v);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LessonViewHolder) {
            final Lesson lesson = (Lesson) listElements.get(position);
//        final Lesson prevLesson = listElements.get(position-1?);
            LessonViewHolder lessonHolder = (LessonViewHolder) holder;
            final Context context = lessonHolder.background.getContext();

            if (lesson == null) {

                //EMPTY LESSON

                lessonHolder.lessonNumber.setVisibility(View.GONE);
                lessonHolder.badge.setVisibility(View.GONE);
                lessonHolder.lessonTeacher.setVisibility(View.GONE);
                lessonHolder.lessonSubject.setVisibility(View.GONE);
                lessonHolder.lessonEmpty.setVisibility(View.VISIBLE);
                lessonHolder.background.setAlpha(0.5f);//TODO
            } else {

                //LESSON

                lessonHolder.lessonNumber.setText(String.valueOf(lesson.getLessonNumber()));
                lessonHolder.lessonSubject.setText(lesson.getSubject().getName());
                lessonHolder.lessonTeacher.setText(lesson.getTeacher().getName());

                if (lesson.isCanceled()) {

                    //canceled

                    lessonHolder.lessonSubject.setPaintFlags(lessonHolder.lessonSubject.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    lessonHolder.lessonTeacher.setPaintFlags(lessonHolder.lessonTeacher.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                    lessonHolder.background.setAlpha(0.5f); //TODO
                    lessonHolder.badge.setVisibility(View.VISIBLE);
                    lessonHolder.badgeText.setText("odwołane");
                    lessonHolder.badgeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_cancel_black_24dp, context.getTheme()));
                } else {
                    //not canceled
                    lessonHolder.lessonSubject.setPaintFlags(lessonHolder.lessonSubject.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    lessonHolder.lessonTeacher.setPaintFlags(lessonHolder.lessonSubject.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    lessonHolder.background.setAlpha(1f);
                    lessonHolder.badge.setVisibility(View.GONE);
                }
                if (lesson.getEvent() != null) {

                    //event

                    lessonHolder.badge.setVisibility(View.VISIBLE);
                    lessonHolder.badgeText.setText(eventCategoryMap.get(lesson.getEvent().getCategoryId()).getName());
                    lessonHolder.badgeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_event_black_24dp, context.getTheme()));
                } else {
                    //no event
                    lessonHolder.badge.setVisibility(View.GONE);
                }
            }
            if (lesson.isSubstitution()) {

                //substitution

                lessonHolder.badge.setVisibility(View.VISIBLE);
                lessonHolder.badgeText.setText("zastępstwo");
                lessonHolder.badgeIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_swap_horiz_black_24dp, context.getTheme()));
            } else {

                //no substitution

                lessonHolder.badge.setVisibility(View.GONE);
            }
            if (!lesson.isCanceled()) {
                lessonHolder.background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDialog.Builder builder = new MaterialDialog.Builder(context).title(lesson.getSubject().getName()).positiveText("Zamknij");

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View details = inflater.inflate(R.layout.lesson_details, null);

                        TextView teacherView = (TextView) details.findViewById(R.id.details_teacher);
                        TextView subjectView = (TextView) details.findViewById(R.id.details_subject);
                        RelativeLayout subjectContainer = (RelativeLayout) details.findViewById(R.id.details_subject_container);
                        TextView date = (TextView) details.findViewById(R.id.details_date);
                        TextView startTime = (TextView) details.findViewById(R.id.details_start_time);
                        TextView endTime = (TextView) details.findViewById(R.id.details_end_time);
                        TextView lessonNumber = (TextView) details.findViewById(R.id.details_lesson_number);
                        LinearLayout event = (LinearLayout) details.findViewById(R.id.event);

                        teacherView.setText(lesson.getTeacher().getName());
                        subjectView.setText(lesson.getSubject().getName());
                        date.setText(lesson.getDate().toString("EEEE, d MMMM yyyy", new Locale("pl")));
                        startTime.setText(lesson.getStartTime().toString("HH:mm"));
                        endTime.setText(" - " + lesson.getEndTime().toString("HH:mm"));
                        lessonNumber.setText(lesson.getLessonNumber() + ". lekcja");
                        if (lesson.getEvent() != null) {
                            TextView eventName = (TextView) details.findViewById(R.id.details_event_name);
                            TextView eventDescription = (TextView) details.findViewById(R.id.details_event_description);
                            TextView addedBy = (TextView) details.findViewById(R.id.details_event_addedBy);

                            event.setVisibility(View.VISIBLE);
                            eventName.setText(eventCategoryMap.get(lesson.getEvent().getCategoryId()).getName());
                            eventDescription.setText(lesson.getEvent().getDescription());
                            addedBy.setText(teacherMap.get(lesson.getEvent().getAddedById()).getName());

                        } else {
                            event.setVisibility(View.GONE);
                        }
                        if (lesson.isSubstitution()) {
                            Teacher orgTeacher = teacherMap.get(lesson.getOrgTeacherId());
                            Subject orgSubject = subjectMap.get(lesson.getOrgSubjectId());
                            teacherView.setText(Html.fromHtml(orgTeacher.getName() + " -> <b>" + lesson.getTeacher().getName() + "</b>"));
                            subjectView.setText(Html.fromHtml(orgSubject.getName() + " -> <b>" + lesson.getSubject().getName() + "</b>"));
                        } else {
                            teacherView.setTypeface(teacherView.getTypeface(), Typeface.BOLD);
                            subjectView.setTypeface(subjectView.getTypeface(), Typeface.BOLD);
                            teacherView.setText(lesson.getTeacher().getName());
                            subjectView.setText(lesson.getSubject().getName());
                        }
                        builder.customView(details, true).show();
                    }
                });
            }
        } else {
            SubheaderViewHolder subheaderViewHolder = (SubheaderViewHolder) holder;
            subheaderViewHolder.sectionTitle.setText((CharSequence) listElements.get(position));
            subheaderViewHolder.done.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listElements.size();
    }

    @Override
    public int getItemViewType(int position) {
        return listElements.get(position) instanceof Lesson ? VIEW_TYPE_LESSON : VIEW_TYPE_HEADER;
    }

    private static class SubheaderViewHolder extends RecyclerView.ViewHolder {
        final TextView sectionTitle;
        final View done;

        SubheaderViewHolder(View root) {
            super(root);
            sectionTitle = (TextView) root.findViewById(R.id.list_subheader_title);
            done = root.findViewById(R.id.list_subheader_done);
        }
    }

    private static class LessonViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout background;
        final TextView
                lessonSubject;
        final TextView lessonEmpty;
        final TextView lessonTeacher;
        final TextView lessonNumber;
        final TextView badgeText;
        final CardView badge;
        final ImageView badgeIcon;

        LessonViewHolder(View root) {
            super(root);
            lessonSubject = (TextView) root.findViewById(R.id.lessonSubject);
            lessonTeacher = (TextView) root.findViewById(R.id.lessonTeacher);
            lessonNumber = (TextView) root.findViewById(R.id.lessonNumber);
            lessonEmpty = (TextView) root.findViewById(R.id.lessonEmpty);
            badgeText = (TextView) root.findViewById(R.id.badgeText);
            badge = (CardView) root.findViewById(R.id.badge);
            badgeIcon = (ImageView) root.findViewById(R.id.badgeIcon);
            background = (LinearLayout) root.findViewById(R.id.background);
        }
    }
}