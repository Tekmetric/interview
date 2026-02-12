import CircularProgress from "@mui/material/CircularProgress";

export default function Loader() {
  return (
    <div className="flex justify-center items-center h-64">
      <CircularProgress />
    </div>
  );
}
