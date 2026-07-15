import { useTranslation } from '../../i18n/LocaleProvider';
import Select from '../../components/Select';
import { IconButton } from '../../components/Button';
import { IconClose } from '../../components/icons';

// An empty value means "all departments".
export default function DepartmentFilter({ departments, value, onChange }) {
  const { t } = useTranslation();
  const options = [
    { value: '', label: t('filter.allDepartments') },
    ...departments.map((d) => ({ value: String(d.id), label: d.name })),
  ];

  return (
    <div className="flex w-full items-center gap-2 sm:w-auto">
      <Select
        id="department-filter"
        label={t('filter.department')}
        value={value}
        onChange={onChange}
        options={options}
        fluid
        sizeClass="h-11 px-3 sm:min-w-[16rem]"
      />
      {value && (
        <IconButton
          onClick={() => onChange('')}
          aria-label={t('filter.clear')}
          title={t('filter.clear')}
        >
          <IconClose className="size-4" />
        </IconButton>
      )}
    </div>
  );
}
