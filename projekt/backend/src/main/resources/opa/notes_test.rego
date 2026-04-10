package notes_test

import data.notes

test_delete_own_note if {
    notes.allow with input as {
        "user": {"id": "ecc35a40-fc70-4199-b157-a511dcbb7b49"},
        "resource": {"type": "note", "ownerId": "ecc35a40-fc70-4199-b157-a511dcbb7b49"},
        "action": "delete"
    }
}

test_delete_other_note if {
    not notes.allow with input as {
        "user": {"id": "68b8228f-7ef1-46d3-8e3c-6a829374e249"},
        "resource": {"type": "note", "ownerId": "131f568b-c244-4452-94d1-6610438e4814"},
        "action": "delete",
    }
}