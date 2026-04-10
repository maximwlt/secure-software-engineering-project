package notes

default allow = false

allow if {
    input.resource.type == "note"
    input.user.id == input.resource.ownerId
    input.action == "delete"
}

