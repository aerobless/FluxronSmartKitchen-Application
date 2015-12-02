package ch.fluxron.fluxronapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * Creates sections in the device list.
 */
public class SectionedDeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private static final int SECTION_TYPE = 0;

    private boolean valid = true;
    private int sectionResourceId;
    private int textResourceId;
    private RecyclerView.Adapter baseAdapter;
    private SparseArray<Section> deviceSections = new SparseArray<Section>();


    /**
     * Creates a new sectioned adapter
     *
     * @param context           Context
     * @param sectionResourceId Resource Id
     * @param textResourceId    Text Resource Id
     * @param deviceListAdapter Device list adapter
     */
    public SectionedDeviceListAdapter(Context context, int sectionResourceId, int textResourceId,
                                      RecyclerView.Adapter deviceListAdapter) {
        this.sectionResourceId = sectionResourceId;
        this.textResourceId = textResourceId;
        this.baseAdapter = deviceListAdapter;
        this.context = context;

        this.baseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                valid = SectionedDeviceListAdapter.this.baseAdapter.getItemCount() > 0;
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                valid = SectionedDeviceListAdapter.this.baseAdapter.getItemCount() > 0;
                notifyItemRangeChanged(positionStart, itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                valid = SectionedDeviceListAdapter.this.baseAdapter.getItemCount() > 0;
                notifyItemRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                valid = SectionedDeviceListAdapter.this.baseAdapter.getItemCount() > 0;
                notifyItemRangeRemoved(positionStart, itemCount);
            }
        });
    }

    /**
     * Holder for a section
     */
    public static class SectionViewHolder extends RecyclerView.ViewHolder {

        /**
         * Title text view
         */
        public TextView title;

        /**
         * Creates a new view holder
         *
         * @param view            View
         * @param mTextResourceId Resource Id
         */
        public SectionViewHolder(View view, int mTextResourceId) {
            super(view);
            title = (TextView) view.findViewById(mTextResourceId);
        }
    }

    /**
     * Creates a new view holder
     *
     * @param parent   Parent
     * @param typeView View type
     * @return Holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int typeView) {
        if (typeView == SECTION_TYPE) {
            final View view = LayoutInflater.from(context).inflate(sectionResourceId, parent, false);
            return new SectionViewHolder(view, textResourceId);
        } else {
            return baseAdapter.onCreateViewHolder(parent, typeView - 1);
        }
    }

    /**
     * Binds the view holder
     *
     * @param sectionViewHolder Holder
     * @param position          Position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder sectionViewHolder, int position) {
        if (isSectionHeaderPosition(position)) {
            ((SectionViewHolder) sectionViewHolder).title.setText(deviceSections.get(position).title);
        } else {
            baseAdapter.onBindViewHolder(sectionViewHolder, sectionedPositionToPosition(position));
        }

    }

    /**
     * Gets the items view type
     *
     * @param position Position
     * @return Type
     */
    @Override
    public int getItemViewType(int position) {
        return isSectionHeaderPosition(position)
                ? SECTION_TYPE
                : baseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1;
    }

    /**
     * Represents a section
     */
    public static class Section {
        int firstPosition;
        int sectionedPosition;
        CharSequence title;

        /**
         * New section
         *
         * @param firstPosition First position
         * @param title         Title
         */
        public Section(int firstPosition, CharSequence title) {
            this.firstPosition = firstPosition;
            this.title = title;
        }

        /**
         * Gets the title
         *
         * @return Title
         */
        public CharSequence getTitle() {
            return title;
        }
    }

    /**
     * Updates all the sections
     *
     * @param categories categories
     */
    public void updateSections(Map<String, Integer> categories) {
        if (categories.size() >= 1) {
            Section[] sections = new Section[categories.size()];
            int i = 0;
            int position = 0;
            for (Map.Entry<String, Integer> e : categories.entrySet()) {
                sections[i] = new Section(position, e.getKey());
                position += e.getValue();
                i++;
            }
            setSections(sections);
        }
    }

    /**
     * Sets the sections
     *
     * @param sections Sections
     */
    public void setSections(Section[] sections) {
        deviceSections.clear();

        Arrays.sort(sections, new Comparator<Section>() {
            @Override
            public int compare(Section o, Section o1) {
                return (o.firstPosition == o1.firstPosition)
                        ? 0
                        : ((o.firstPosition < o1.firstPosition) ? -1 : 1);
            }
        });

        int offset = 0; // offset positions for the headers we're adding
        for (Section section : sections) {
            section.sectionedPosition = section.firstPosition + offset;
            deviceSections.append(section.sectionedPosition, section);
            ++offset;
        }

        notifyDataSetChanged();
    }

    /**
     * Transforms a sectioned position into a normal one
     *
     * @param position Sectioned position
     * @return Normal position
     */
    public int positionToSectionedPosition(int position) {
        int offset = 0;
        for (int i = 0; i < deviceSections.size(); i++) {
            if (deviceSections.valueAt(i).firstPosition > position) {
                break;
            }
            ++offset;
        }
        return position + offset;
    }

    /**
     * Converts the sectioned position.
     *
     * @param sectionedPosition
     * @return
     */
    public int sectionedPositionToPosition(int sectionedPosition) {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION;
        }

        int offset = 0;
        for (int i = 0; i < deviceSections.size(); i++) {
            if (deviceSections.valueAt(i).sectionedPosition > sectionedPosition) {
                break;
            }
            --offset;
        }
        return sectionedPosition + offset;
    }

    /**
     * Checks for section header at position
     *
     * @param position Position
     * @return Section header found
     */
    public boolean isSectionHeaderPosition(int position) {
        return deviceSections.get(position) != null;
    }

    /**
     * Gets the items id
     *
     * @param position Index
     * @return Id
     */
    @Override
    public long getItemId(int position) {
        return isSectionHeaderPosition(position)
                ? Integer.MAX_VALUE - deviceSections.indexOfKey(position)
                : baseAdapter.getItemId(sectionedPositionToPosition(position));
    }

    /**
     * Gets the number of items in this container
     *
     * @return Number of items
     */
    @Override
    public int getItemCount() {
        return (valid ? baseAdapter.getItemCount() + deviceSections.size() : 0);
    }
}