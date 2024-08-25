import * as Yup from "yup";

export const validationSchema = Yup.object({
  title: Yup.string().required("Event title is required"),
  eventDatetime: Yup.string().required("Event date and time are required"),
  description: Yup.string().required("Description is required"),
  eventImageUrl: Yup.string().url("Invalid URL").nullable(),
});
