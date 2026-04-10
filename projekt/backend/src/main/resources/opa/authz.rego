package authz

default allow = false

allow if {
    input.subject == "admin"
    input.action == "read"
}