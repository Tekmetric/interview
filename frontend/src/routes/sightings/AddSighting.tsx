import SightingForm from "../../components/Forms/SightingForm/SightingForm";

export default function AddSighting() {
  return <SightingForm
    onSave={() => alert("TODO")}
    pandas={[]}
    selectedPandaId={undefined}
  />;
}
