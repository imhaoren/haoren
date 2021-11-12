package com.haoren.ioc.servlet;



import com.haoren.ioc.factory.BeanFactory;
import com.haoren.ioc.proxy.ProxyFactory;
import com.haoren.ioc.service.TransferService;
import com.haoren.ioc.servlet.mo.ResponseMO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "transferServlet", urlPatterns = "/transferServlet")
public class TransferServlet extends HttpServlet {

    private ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getBean("proxyFactory");

    private TransferService transferService = (TransferService) proxyFactory.getProxy(BeanFactory.getBean("transferService"));

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String fromCardNo = req.getParameter("fromCardNo");
        String toCardNo = req.getParameter("toCardNo");
        BigDecimal money = new BigDecimal(req.getParameter("money"));
        ResponseMO responseMO = new ResponseMO();
        try {
            transferService.transfer(fromCardNo, toCardNo, money);
            responseMO.setStatus(200);
        } catch (Exception e) {
            e.printStackTrace();
            responseMO.setStatus(201);
            responseMO.setMessage(e.getMessage());
        }
        resp.getWriter().print(responseMO.toString());
    }
}
