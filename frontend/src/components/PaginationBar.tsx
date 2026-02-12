import Pagination from "@mui/material/Pagination";
import Stack from "@mui/material/Stack";

type Props = {
  page: number;
  pages: number;
  onChange: (event: React.ChangeEvent<unknown>, value: number) => void;
};

export default function PaginationBar({ page, pages, onChange }: Props) {
  return (
    <Stack spacing={2} className="flex justify-center my-8">
      <Pagination
        count={pages}
        page={page}
        onChange={onChange}
        color="primary"
        variant="outlined"
        shape="rounded"
      />
    </Stack>
  );
}
