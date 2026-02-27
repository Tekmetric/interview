type PageTitleProps = {
  title: string
}

export function PageTitle({ title }: PageTitleProps) {
  return (
    <div className="mb-6 flex items-start justify-between gap-4">
      <div>
        <h1 className="text-2xl font-bold">{title}</h1>
      </div>
    </div>
  )
}
