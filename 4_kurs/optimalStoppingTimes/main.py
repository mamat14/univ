import matplotlib.pyplot as plt


def transition_probability(k, l):
    if k >= l:
        return 0
    else:
        return k / (l * (l - 1))


def transition_table(n):
    columns = ['%d' % x for x in range(1, n + 1)]
    rows = columns.copy()
    data = [[transition_probability(i, j) for i in range(1, n + 1)] for j in range(1, n + 1)]
    return columns, rows, data


def draw_table(columns, rows, data):
    n = len(data)
    fig, ax = plt.subplots()
    ax.axis('tight')
    ax.axis('off')
    ax.table(cellText=[['%1.2f' % data[i][j] for i in range(0, n)] for j in range(0, n)],
             rowLabels=rows,
             colLabels=columns,
             loc='center')
    ax.set_title('Transitional probabilities')
    fig.show()


def stop(k, n):
    return k / n


def draw_q(n, ax):
    ax.plot([i for i in range(1, n + 1)], [stop(k, n) for k in range(1, n + 1)])


def not_stop(k, n):
    if k < n:
        return stop(k, n) * sum([1 / k for k in range(k, n)])
    else:
        return 0


def draw_not_stop(n, ax):
    ax.plot([i for i in range(1, n + 1)], [not_stop(k, n) for k in range(1, n + 1)])


def to_draw_table(n):
    return n < 10


def draw_resulting_plot(n):
    fig, ax = plt.subplots()
    draw_q(n, ax)
    draw_not_stop(n, ax)
    fig.show()


def draw_resulting_table(n):
    tt = transition_table(n)
    if to_draw_table(n):
        draw_table(*tt)
    draw_resulting_plot(n)


n = 250
draw_resulting_table(n)