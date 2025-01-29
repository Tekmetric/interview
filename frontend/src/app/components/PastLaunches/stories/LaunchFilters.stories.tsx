import LaunchFilters from '../LaunchFilters'

export default {
  title: 'Components/LaunchFilters',
  component: LaunchFilters,
}

export const Default = () => (
  <LaunchFilters
    onFilterChange={(filters) => console.log(filters)}
    onSortChange={(sort) => console.log(sort)}
  />
)
