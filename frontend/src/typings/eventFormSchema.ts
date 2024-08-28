import * as Yup from "yup";

export const validationSchema = Yup.object({
  title: Yup.string()
    .test(
      "is-not-empty-or-whitespace",
      "Title cannot be empty or contain only spaces.",
      (value) => {
        return (value?.trim()?.length ?? 0) > 0;
      }
    )
    .required("Event title is required"),
  eventDatetime: Yup.string().required("Event date and time are required"),
  description: Yup.string()
    .test(
      "is-not-empty-or-whitespace",
      "Description cannot be empty or contain only spaces.",
      (value) => {
        return (value?.trim()?.length ?? 0) > 0;
      }
    )
    .required("Description is required"),
  eventImageUrl: Yup.string().url("Invalid URL").nullable(),
});
