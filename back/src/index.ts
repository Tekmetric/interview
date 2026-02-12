import app from "./app";

const PORT = process.env.PORT || 4000;
app.listen(PORT, () => console.log(`The backend server is running on:${PORT}`));

