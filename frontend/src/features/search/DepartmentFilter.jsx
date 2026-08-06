import Select from '../../components/Select';
import { IconButton } from '../../components/Button';
import { IconClose } from '../../components/icons';

// An empty value means "all departments".
export default function DepartmentFilter({ departments, value, onChange }) {
  const options = [
    { value: '', label: 'All departments' },
    ...departments.map((d) => ({ value: String(d.id), label: d.name })),
  ];

  return (
    <div className="flex w-full items-center gap-2 sm:w-auto">
      <Select
        id="department-filter"
        label="Department"
        value={value}
        onChange={onChange}
        options={options}
        fluid
        sizeClass="h-11 px-3 sm:min-w-[16rem]"
      />
      {value && (
        <IconButton
          onClick={() => onChange('')}
          aria-label="Clear department filter"
          title="Clear department filter"
        >
          <IconClose className="size-4" />
        </IconButton>
      )}
    </div>
  );
}
