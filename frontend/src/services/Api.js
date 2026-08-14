const API_BASE_URL = "/api";

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

        let message = `Request failed: ${response.status}`;

try {
    const data = JSON.parse(text);

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

    health: () =>
        request("/health"),

    getGroups: () =>
        request("/groups"),

    getGroup: (groupId) =>
        request(`/groups/${groupId}`),

    createGroup: (name) =>
        request("/groups", {
            method: "POST",
            body: JSON.stringify({
                name
            })
            
        }),
        deleteGroup: (groupId) =>
    request(
        `/groups/${groupId}`,
        {
            method: "DELETE"
        }
    ),

    getMembers: (groupId) =>
        request(`/groups/${groupId}/members`),

    addMember: (groupId, member) =>
        request(`/groups/${groupId}/members`, {
            method: "POST",
            body: JSON.stringify(member)
        }),
        deleteMember: (
    groupId,
    studentId
) =>
    request(
        `/groups/${groupId}/members/${studentId}`,
        {
            method: "DELETE"
        }
    ),

    getExpenses: (groupId) =>
        request(`/groups/${groupId}/expenses`),

    addExpense: (groupId, expense) =>
        request(`/groups/${groupId}/expenses`, {
            method: "POST",
            body: JSON.stringify(expense)
        }),

    deleteExpense: (groupId, expenseId) =>
        request(
            `/groups/${groupId}/expenses/${expenseId}`,
            {
                method: "DELETE"
            }
        ),

    getBalances: (groupId) =>
        request(`/groups/${groupId}/balances`),

    getSettlements: (groupId) =>
        request(`/groups/${groupId}/settlements`)
};