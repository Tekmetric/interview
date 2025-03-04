export const Staff = ({ staff }) => {
  return (
    <>
      <p className="font-bold text-lg border-b mt-8 mb-4">Staff</p>
      <div className="grid grid-cols-2 gap-3">
        {staff.map((staffMember) => {
          return (
            <div className="flex w-full justify-between border-2" key={staffMember.person.mal_id}>
              <div className="flex">
                <img className="w-12 mr-4" src={staffMember.person.images.jpg.image_url} alt="staff_member_image" />
                <div className="max-w-52">
                  <p className="font-bold">{staffMember.person.name}</p>
                  <p className="text-sm">{staffMember.positions[0]}</p>
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </>
  )
};