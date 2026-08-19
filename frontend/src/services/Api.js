const API_BASE_URL =
    import.meta.env.VITE_API_URL || "/api";

async function request(endpoint, options = {}) {

    const response = await fetch(
        `${API_BASE_URL}${endpoint}`,
        {
            headers: {
                "Content-Type": "application/json",
                ...(options.headers || {})
            },
            ...options
        }
    );

    if (!response.ok) {

        const text =
            await response.text();

        let message =
            `Request failed: ${response.status}`;

        try {

            const data =
                JSON.parse(text);

            if (data.message) {
                message = data.message;
            } else if (data.error) {
                message = data.error;
            }

        } catch {

            if (text) {
                message = text;
            }
        }

        throw new Error(message);
    }

    const contentType =
        response.headers.get("content-type");

    if (
        contentType &&
        contentType.includes("application/json")
    ) {
        return response.json();
    }

    return response.text();
}


export const api = {

    // =========================
    // AUTHENTICATION
    // =========================

    signup: (user) =>
        request("/auth/signup", {
            method: "POST",
            body: JSON.stringify(user)
        }),

    login: (credentials) =>
        request("/auth/login", {
            method: "POST",
            body: JSON.stringify(credentials)
        }),


    // =========================
    // HEALTH
    // =========================

    health: () =>
        request("/health"),


    // =========================
    // GROUPS
    // =========================

    getGroups: (userId) =>
        request(
            `/groups?userId=${userId}`
        ),

    getGroup: (groupId, userId) =>
        request(
            `/groups/${groupId}?userId=${userId}`
        ),

    createGroup: (name, userId) =>
        request(
            `/groups?userId=${userId}`,
            {
                method: "POST",

                body: JSON.stringify({
                    name
                })
            }
        ),

    deleteGroup: (groupId, userId) =>
        request(
            `/groups/${groupId}?userId=${userId}`,
            {
                method: "DELETE"
            }
        ),


    // =========================
    // MEMBERS
    // =========================

    getMembers: (groupId) =>
        request(
            `/groups/${groupId}/members`
        ),

    addMember: (groupId, member) =>
        request(
            `/groups/${groupId}/members`,
            {
                method: "POST",

                body: JSON.stringify(
                    member
                )
            }
        ),

    deleteMember: (
    groupId,
    studentId,
    userId
) =>
    request(
        `/groups/${groupId}/members/${studentId}?userId=${userId}`,
        {
            method: "DELETE"
        }
    ),

    // =========================
    // EXPENSES
    // =========================

    getExpenses: (groupId) =>
        request(
            `/groups/${groupId}/expenses`
        ),

    addExpense: (
        groupId,
        expense
    ) =>
        request(
            `/groups/${groupId}/expenses`,
            {
                method: "POST",

                body: JSON.stringify(
                    expense
                )
            }
        ),

    deleteExpense: (
        groupId,
        expenseId
    ) =>
        request(
            `/groups/${groupId}/expenses/${expenseId}`,
            {
                method: "DELETE"
            }
        ),


    // =========================
    // BALANCES
    // =========================

    getBalances: (groupId) =>
        request(
            `/groups/${groupId}/balances`
        ),


    // =========================
    // SETTLEMENTS
    // =========================

    getSettlements: (groupId) =>
        request(
            `/groups/${groupId}/settlements`
        )
};